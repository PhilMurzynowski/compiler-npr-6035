package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.*;

public class DeadCode implements Optimization {

  /**
   *
   * @param llBasicBlock
   * @param entryBitMap
   * @param exitBitMap
   * @return predecessors of this basic block
   */
  public static boolean apply(LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap) {
    List<LLInstruction> allInstructions = llBasicBlock.getInstructions();
    List<LLInstruction> aliveInstructions = new ArrayList<>();

    BitMap<LLDeclaration> currentBitMap = new BitMap<>(exitBitMap);

    for (int i = allInstructions.size() - 1; i >= 0; i--) {
      LLInstruction instruction = allInstructions.get(i);
      boolean isCall = instruction instanceof LLInternalCall || instruction instanceof LLExternalCall;
      boolean isStore = instruction instanceof LLStoreArray || instruction instanceof LLStoreScalar;
      if (!isCall && !isStore && instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if (currentBitMap.get(definition)) {
          currentBitMap.clear(definition);  // because old definition is changed
          aliveInstructions.add(instruction);
          for (LLDeclaration use: instruction.uses()) {
            currentBitMap.set(use);
          }
        }
      } else {
        // returns, exceptions, comparisons with no definition
        aliveInstructions.add(instruction);
        for (LLDeclaration use: instruction.uses()) {
          currentBitMap.set(use);
        }
      }
    }

    Collections.reverse(aliveInstructions);
    llBasicBlock.setInstructions(aliveInstructions);

    if (currentBitMap.sameValue(entryBitMap)) {
      return false;
    } else {
      entryBitMap.subsume(currentBitMap);
      return true;
    }
  }

  /**
   *
   * @param controlFlowGraph
   * @param globals a list of global field declarations
   */
  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    final Map<LLBasicBlock, BitMap<LLDeclaration>> entryBitMaps = new HashMap<>();
    final Map<LLBasicBlock, BitMap<LLDeclaration>> exitBitMaps = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    // Initialize the exit block's bit maps
    final BitMap<LLDeclaration> globalExitBitMap = new BitMap<>();
    for (LLDeclaration global : globals) {
      globalExitBitMap.set(global);
    }
    entryBitMaps.put(controlFlowGraph.getExit(), new BitMap<>());
    exitBitMaps.put(controlFlowGraph.getExit(), globalExitBitMap);

    workSet.addAll(controlFlowGraph.getExit().getPredecessors());

    visited.add(controlFlowGraph.getExit());

    // Initialize all blocks' bit maps
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        entryBitMaps.put(block, new BitMap<>());
        exitBitMaps.put(block, new BitMap<>());

        workSet.addAll(block.getPredecessors());

        visited.add(block);
      }
    }

    // Set all blocks as to-be-visited
    workSet.addAll(visited);

    // Apply DCE to all blocks in work set
    while (!workSet.isEmpty()) {
      LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      // Only update predecessors if entryBitMap changes
      if (apply(block, entryBitMaps.get(block), exitBitMaps.get(block))) {
        for (LLBasicBlock p : block.getPredecessors()) {
          BitMap<LLDeclaration> exitMap = exitBitMaps.get(p);
          exitMap.or(entryBitMaps.get(block));

          // Add all predecessors to work set
          workSet.add(p);
        }
      }
    }
  }

}
