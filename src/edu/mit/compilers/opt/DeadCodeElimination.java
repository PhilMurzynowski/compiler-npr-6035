package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class DeadCodeElimination implements Optimization {

  /**
   *
   * @param llBasicBlock
   * @param entryBitMap
   * @param exitBitMap
   * @return predecessors of this basic block
   */
  public static boolean update(LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap, boolean delete) {
    List<LLInstruction> allInstructions = llBasicBlock.getInstructions();
    List<LLInstruction> aliveInstructions = new ArrayList<>();

    BitMap<LLDeclaration> currentBitMap = new BitMap<>(exitBitMap);

    for (int i = allInstructions.size() - 1; i >= 0; i--) {
      LLInstruction instruction = allInstructions.get(i);
      boolean isCall = instruction instanceof LLInternalCall || instruction instanceof LLExternalCall;
      boolean isStoreArray = instruction instanceof LLStoreArray;
      if (!isCall && instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if (currentBitMap.get(definition)) {
          // Only clear if not a store array
          if (!isStoreArray) {
            currentBitMap.clear(definition);  // because old definition is changed
          }
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
    if (delete) {
      llBasicBlock.setInstructions(aliveInstructions);
    }

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

    if (controlFlowGraph.hasExit()) {
      entryBitMaps.put(controlFlowGraph.expectExit(), new BitMap<>());
      exitBitMaps.put(controlFlowGraph.expectExit(), globalExitBitMap);

      workSet.addAll(controlFlowGraph.expectExit().getPredecessors());
      visited.add(controlFlowGraph.expectExit());
    }

    // Initialize the exception blocks' bit maps
    for (LLBasicBlock block : controlFlowGraph.getExceptions()) {
      entryBitMaps.put(block, new BitMap<>());
      exitBitMaps.put(block, new BitMap<>());

      workSet.addAll(block.getPredecessors());
      visited.add(block);
    }

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

    // Update entry/exit sets for all basic blocks
    while (!workSet.isEmpty()) {
      LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      // Only update predecessors if entryBitMap changes
      if (update(block, entryBitMaps.get(block), exitBitMaps.get(block), false)) {
        for (LLBasicBlock p : block.getPredecessors()) {
          BitMap<LLDeclaration> exitMap = exitBitMaps.get(p);
          exitMap.or(entryBitMaps.get(block));

          // Add all predecessors to work set
          workSet.add(p);
        }
      }
    }

    // Now actually delete dead code
    for (LLBasicBlock block : visited) {
      boolean updated = update(block, entryBitMaps.get(block), exitBitMaps.get(block), true);
      assert !updated : "nothing should change at this point";
    }
  }

}
