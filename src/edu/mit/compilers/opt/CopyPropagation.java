package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class CopyPropagation implements Optimization {

  private static boolean update(LLBasicBlock basicBlock, BitMap<LLInstruction> entry, BitMap<LLInstruction> exit, boolean propagate, Set<LLDeclaration> globals) {
    final Map<LLDeclaration, Set<LLInstruction>> definitionInstructions = new HashMap<>();

    for (LLInstruction definitionInstruction : entry.trueSet()) {
      assert definitionInstruction.definition().isPresent() : "should only contain definitions";

      final LLDeclaration definition = definitionInstruction.definition().get();

      if (!definitionInstructions.containsKey(definition)) {
        definitionInstructions.put(definition, new HashSet<>());
      }

      definitionInstructions.get(definition).add(definitionInstruction);
    }

    final BitMap<LLInstruction> current = new BitMap<>(entry);
    final List<LLInstruction> newInstructions = new ArrayList<>();

    for (LLInstruction instruction : basicBlock.getInstructions()) {
      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (definitionInstructions.containsKey(definition)) {
          for (LLInstruction definitionInstruction : definitionInstructions.get(definition)) {
            current.clear(definitionInstruction);
          }
        }

        current.set(instruction);
        definitionInstructions.put(definition, new HashSet<>(Set.of(instruction)));
      }

      if (propagate) {
        final List<LLDeclaration> newUses = new ArrayList<>();

        for (LLDeclaration use : instruction.uses()) {
          LLDeclaration newUse = use;
          while (!globals.contains(newUse) && definitionInstructions.containsKey(newUse) && definitionInstructions.get(newUse).size() == 1) {
            final LLInstruction definitionInstruction = definitionInstructions.get(newUse).iterator().next();
            if (definitionInstruction instanceof LLIntegerLiteral integerLiteral) {
              newUse = new LLConstantDeclaration(integerLiteral.getValue());
              break;
            } else if (definitionInstruction instanceof LLStoreScalar storeScalar) {
              newUse = storeScalar.uses().iterator().next();
            } else if (definitionInstruction instanceof LLLoadScalar loadScalar) {
              newUse = loadScalar.uses().iterator().next();
            } else {
              break;
            }
          }
          newUses.add(newUse);
        }

        newInstructions.add(instruction.usesReplaced(newUses));
      }
    }

    if (propagate) {
      basicBlock.setInstructions(newInstructions);
    }

    if (current.sameValue(exit)) {
      return false;
    } else {
      exit.subsume(current);
      return true;
    }
  }

  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    final Map<LLBasicBlock, BitMap<LLInstruction>> entries = new HashMap<>();
    final Map<LLBasicBlock, BitMap<LLInstruction>> exits = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    workSet.add(controlFlowGraph.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        entries.put(block, new BitMap<>());
        exits.put(block, new BitMap<>());

        workSet.addAll(block.getSuccessors());

        visited.add(block);
      }
    }

    workSet.addAll(visited);

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (update(block, entries.get(block), exits.get(block), false, new HashSet<>(globals))) {
        for (LLBasicBlock successor : block.getSuccessors()) {
          entries.get(successor).or(exits.get(block));

          workSet.add(successor);
        }
      }
    }

    for (LLBasicBlock block : visited) {
      boolean updated = update(block, entries.get(block), exits.get(block), true, new HashSet<>(globals));
      assert !updated : "nothing should change at this point";
    }
  }

}
