package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class CopyPropagation implements Optimization {

  private static LLDeclaration propagate(LLDeclaration use, Map<LLDeclaration, Set<LLInstruction>> definitionInstructions, Set<LLDeclaration> globals) {
    if (!globals.contains(use) && definitionInstructions.containsKey(use) && definitionInstructions.get(use).size() == 1) {
      final LLInstruction definitionInstruction = definitionInstructions.get(use).iterator().next();
      if (definitionInstruction instanceof LLIntegerLiteral integerLiteral) {
        return new LLConstantDeclaration(integerLiteral.getValue());
      } else if (definitionInstruction instanceof LLLength length) {
        return new LLConstantDeclaration(length.getDeclaration().getLength());
      } else if (definitionInstruction instanceof LLStringLiteral stringLiteral) {
        return stringLiteral.getDeclaration();
      } else if (definitionInstruction instanceof LLStoreScalar storeScalar) {
        return propagate(storeScalar.uses().iterator().next(), definitionInstructions, globals);
      } else if (definitionInstruction instanceof LLLoadScalar loadScalar) {
        return propagate(loadScalar.uses().iterator().next(), definitionInstructions, globals);
      } else if (definitionInstruction instanceof LLCopy copy) {
        return copy.uses().iterator().next();
      } else {
        return use;
      }
    } else {
      return use;
    }
  }

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
      LLInstruction newInstruction = null; // NOTE(rbd): This will always be initialized before use, but Java doesn't believe me...

      if (propagate) {
        final List<LLDeclaration> newUses = new ArrayList<>();

        for (LLDeclaration use : instruction.uses()) {
          newUses.add(propagate(use, definitionInstructions, globals));
        }

        newInstruction = instruction.usesReplaced(newUses);
        newInstructions.add(newInstruction);
      }

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (definitionInstructions.containsKey(definition)) {
          for (LLInstruction definitionInstruction : definitionInstructions.get(definition)) {
            current.clear(definitionInstruction);
          }
        }

        if (propagate) {
          current.set(newInstruction);
          definitionInstructions.put(definition, new HashSet<>(Set.of(newInstruction)));
        } else {
          current.set(instruction);
          definitionInstructions.put(definition, new HashSet<>(Set.of(instruction)));
        }
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
      update(block, entries.get(block), exits.get(block), true, new HashSet<>(globals));
    }
  }

}
