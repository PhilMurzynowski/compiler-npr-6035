package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.*;

public class CommonSubExpression implements Optimization {

  // Perform GEN KILL update for available expressions
  public static boolean update(LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExpr, BitMap<String> entryBitMap, BitMap<String> exitBitMap) {

    BitMap<String> currentBitMap = new BitMap<>(entryBitMap);
    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      if (instruction instanceof LLBinary || instruction instanceof LLUnary || instruction instanceof LLCompare) {
        String expr = instruction.getUniqueExpressionString();
        // GEN
        currentBitMap.set(expr);
      }

      if (instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if(mapVarToExpr.containsKey(definition)) {
          for (String expr : mapVarToExpr.get(definition)) {
            // KILL
            exitBitMap.clear(expr);
          }
        }
      }
    }
    if (currentBitMap.sameValue(entryBitMap)) {
      return false;
    } else {
      entryBitMap.subsume(currentBitMap);
      return true;
    }
  }

  // Initialize maps with all expressions in a basic block
  public static void initBitMap(LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExpr, BitMap<String> bitMap) {

    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      if (instruction instanceof LLBinary || instruction instanceof LLUnary || instruction instanceof LLCompare) {
        String expr = instruction.getUniqueExpressionString();
        // track all variables used in expr, if change need to KILL expr
        for (LLDeclaration use : instruction.uses()) {
          if (mapVarToExpr.containsKey(use)) {
            mapVarToExpr.get(use).add(expr); 
          } else {
            mapVarToExpr.put(use, List.of(expr));
          }

        // assume available everywhere initially (optimisic)
        bitMap.set(expr);
        }
      }
    }
  }

  // Initialize maps with all expressions in a CFG
  public static void initBitMap(LLControlFlowGraph controlFlowGraph, Map<LLDeclaration, List<String>> mapVarToExpr, BitMap<String> bitMap) {

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    workSet.add(controlFlowGraph.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        initBitMap(block, mapVarToExpr, bitMap);
        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }

  }

  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    final Map<LLBasicBlock, BitMap<String>> entryBitMaps = new HashMap<>();
    final Map<LLBasicBlock, BitMap<String>> exitBitMaps = new HashMap<>();

    final Map<LLDeclaration, List<String>> mapVarToExpr = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    // Initialize the entry block's bit maps
    final BitMap<String> globalEntryInBitMap = new BitMap<>();
    final BitMap<String> globalEntryOutBitMap = new BitMap<>();
    update(controlFlowGraph.getEntry(), mapVarToExpr, globalEntryInBitMap, globalEntryOutBitMap);
    entryBitMaps.put(controlFlowGraph.getEntry(), globalEntryInBitMap); 
    exitBitMaps.put(controlFlowGraph.getExit(), globalEntryOutBitMap); 
    workSet.addAll(controlFlowGraph.getExit().getSuccessors());
    visited.add(controlFlowGraph.getEntry());

    // Initialize all other bitmaps
    BitMap<String> defaultBitMap = new BitMap<>();
    initBitMap(controlFlowGraph, mapVarToExpr, defaultBitMap);
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        BitMap<String> defaultEntryBitMap = new BitMap<>(defaultBitMap);
        BitMap<String> defaultExitBitMap = new BitMap<>(defaultBitMap);
        entryBitMaps.put(block, defaultEntryBitMap); 
        exitBitMaps.put(block, defaultExitBitMap);

        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }

    // Set all blocks as to-be-visited
    workSet.addAll(visited);

    // Update entry/exit sets for all basic blocks
    while (!workSet.isEmpty()) {
      LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      // Only update successors if entryBitMap changes
      if (update(block, mapVarToExpr, entryBitMaps.get(block), exitBitMaps.get(block))) {
        for (LLBasicBlock s : block.getSuccessors()) {
          BitMap<String> exitMap = exitBitMaps.get(s);
          exitMap.and(entryBitMaps.get(block));

          // Add all sucessors to work set
          workSet.add(s);
        }
      }
    }
  }

  // NOTE(phil): DEPRECATED, value numbering method, does not update bitmap, kept to reuse snippets and in case needed later
  public static boolean transform(LLMethodDeclaration methodDeclaration, LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap)
  {

    CSETable cseTable = new CSETable(methodDeclaration); 

    List<LLInstruction> newLLInstructions = new ArrayList<>();
    BitMap<LLDeclaration> currentBitMap = new BitMap<>(entryBitMap);

    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      StringBuilder exprBuilder = new StringBuilder();

      if (instruction instanceof LLBinary binaryInstruction) {

        exprBuilder.append(cseTable.varToVal(binaryInstruction.getLeft()));
        exprBuilder.append(binaryInstruction.getType());
        exprBuilder.append(cseTable.varToVal(binaryInstruction.getRight()));

      } else if (instruction instanceof LLUnary unaryInstruction) {

        UnaryExpressionType type = unaryInstruction.getType();
        if (type == UnaryExpressionType.NOT || type ==  UnaryExpressionType.NEGATE) {
          exprBuilder.append(type);
          exprBuilder.append(cseTable.varToVal(unaryInstruction.getExpression()));
        } else if (type == UnaryExpressionType.INCREMENT || type == UnaryExpressionType.INCREMENT) {
          exprBuilder.append(cseTable.varToVal(unaryInstruction.getExpression()));
          exprBuilder.append(type);
        } else {
          throw new RuntimeException("unreachable");
        }
 
      } else if (instruction instanceof LLCompare cmpInstruction) {

        exprBuilder.append(cseTable.varToVal(cmpInstruction.getLeft()));
        exprBuilder.append(cmpInstruction.getType().toBinaryExpressionType());
        exprBuilder.append(cseTable.varToVal(cmpInstruction.getRight()));

      } else {

        newLLInstructions.add(instruction);
        continue;

      }

      String expr = exprBuilder.toString();
      cseTable.exprToVal(expr);

      // NOTE(phil): may duplicate instructions if pass over same BB
      if (!cseTable.inExprToTmp(expr)) {
        LLDeclaration tmp = cseTable.addExprToTmp(expr);
        LLCopy copyInstruction = new LLCopy(instruction.definition().get(), tmp);
        newLLInstructions.add(instruction);
        newLLInstructions.add(copyInstruction);
      } else {
        LLDeclaration tmp = cseTable.getExprToTmp(expr);
        // modified instruction as no longer using LLBinary, LLUnary, etc, just the tmp
        LLCopy modifiedInstruction = new LLCopy(tmp, instruction.definition().get());
        newLLInstructions.add(modifiedInstruction);
      }

    }

    llBasicBlock.setInstructions(newLLInstructions);

    if (currentBitMap.sameValue(exitBitMap)) {
      return false;
    } else {
      exitBitMap.subsume(currentBitMap);
      return true;
    }
  }
}