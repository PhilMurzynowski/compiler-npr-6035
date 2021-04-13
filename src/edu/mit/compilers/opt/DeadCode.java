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
  public static Set<LLBasicBlock> apply(LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap) {

    List<LLInstruction> allLLInstructions = llBasicBlock.getInstructions();
    List<LLInstruction> aliveLLInstructions = new ArrayList<>();
    entryBitMap.subsume(exitBitMap);
    boolean changed = false;

    for (int i = allLLInstructions.size() - 1; i >= 0; i--) {
      LLInstruction instruction = allLLInstructions.get(i);
      boolean isCall = instruction instanceof LLInternalCall || instruction instanceof LLExternalCall;
      if (!isCall && instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if (entryBitMap.get(definition)) {
          entryBitMap.clear(definition);  // because old definition is changed
          aliveLLInstructions.add(instruction);
          for (LLDeclaration use: instruction.uses()) {
            entryBitMap.set(use);
          }
        } else {
          changed = true;
        }
      } else {
        // returns, exceptions, comparisons with no definition
        aliveLLInstructions.add(instruction);
        for (LLDeclaration use: instruction.uses()) {
          entryBitMap.set(use);
        }
      }
    }

    Collections.reverse(aliveLLInstructions);
    llBasicBlock.setInstructions(aliveLLInstructions);

    return changed ? llBasicBlock.getPredecessors() : new HashSet<>();
    
  }

  /**
   *
   * @param controlFlowGraph
   * @param globals a list of global field declarations
   */
  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    workSet.add(controlFlowGraph.getExit());

    Map<LLBasicBlock, BitMap<LLDeclaration>> entryBitMaps = new HashMap<>();
    Map<LLBasicBlock, BitMap<LLDeclaration>> exitBitMaps = new HashMap<>();

    BitMap<LLDeclaration> globalExitBitMap = new BitMap<>();
    for (LLDeclaration global : globals) {
      globalExitBitMap.set(global);
    }
    entryBitMaps.put(controlFlowGraph.getExit(), new BitMap<>());
    exitBitMaps.put(controlFlowGraph.getExit(), globalExitBitMap);

    while (!workSet.isEmpty()) {
      LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!exitBitMaps.containsKey(block)) {
        entryBitMaps.put(block, new BitMap<>());
        exitBitMaps.put(block, new BitMap<>());
      }

      Set<LLBasicBlock> predecessors = apply(block, entryBitMaps.get(block), exitBitMaps.get(block));

      for (LLBasicBlock p : predecessors) {
        if (!exitBitMaps.containsKey(p)) {
          entryBitMaps.put(p, new BitMap<>());
          exitBitMaps.put(p, new BitMap<>());
        }

        BitMap<LLDeclaration> exitMap = exitBitMaps.get(p);
        exitMap.or(entryBitMaps.get(block));
      }

      workSet.addAll(predecessors);
    }

  }

}
