package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.*;


public class CommonSubExpression implements Optimization {

  // Perform GEN KILL update for available expressions
  public static boolean update(LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExprs, BitMap<String> entryBitMap, BitMap<String> exitBitMap) {

    BitMap<String> currentBitMap = new BitMap<>(entryBitMap);
    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      if (instruction instanceof LLBinary || instruction instanceof LLUnary || instruction instanceof LLCompare) {
        String expr = instruction.getUniqueExpressionString();
        // GEN
        // track all variables used in expr, if change need to KILL expr
        for (LLDeclaration use : instruction.uses()) {
          if (mapVarToExprs.containsKey(use)) {
            //mapVarToExprs.get(use).add(expr);  I wish java let me do this hahah
            List<String> exprs = new ArrayList<>(mapVarToExprs.get(use));
            exprs.add(expr);
            mapVarToExprs.put(use, exprs);
          } else {
            mapVarToExprs.put(use, List.of(expr));
          }
        }
        currentBitMap.set(expr);
      }

      if (instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if(mapVarToExprs.containsKey(definition)) {
          for (String expr : mapVarToExprs.get(definition)) {
            // KILL
            //System.out.println("Killing\n");
            //System.out.println(llBasicBlock.prettyString(0));
            currentBitMap.clear(expr);
          }
        }
      }
    }

    if (currentBitMap.sameValue(exitBitMap)) {
      return false;
    } else {
      exitBitMap.subsume(currentBitMap);
      return true;
    }
  }

  // Initialize maps with all expressions in a basic block
  public static void initBitMap(LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExprs, BitMap<String> bitMap) {

    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      if (instruction instanceof LLBinary || instruction instanceof LLUnary || instruction instanceof LLCompare) {
        String expr = instruction.getUniqueExpressionString();
        // track all variables used in expr, if change need to KILL expr
        for (LLDeclaration use : instruction.uses()) {
          if (mapVarToExprs.containsKey(use)) {
            List<String> exprs = new ArrayList<>(mapVarToExprs.get(use));
            exprs.add(expr);
            mapVarToExprs.put(use, exprs);
          } else {
            mapVarToExprs.put(use, List.of(expr));
          }

        // assume available everywhere initially (optimisic)
        bitMap.set(expr);
        }
      }
    }
  }

  // Initialize maps with all expressions in a CFG
  public static void initBitMap(LLControlFlowGraph controlFlowGraph, Map<LLDeclaration, List<String>> mapVarToExprs, BitMap<String> bitMap) {

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    workSet.add(controlFlowGraph.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        initBitMap(block, mapVarToExprs, bitMap);
        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }

  }

  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    final Map<LLBasicBlock, BitMap<String>> entryBitMaps = new HashMap<>();
    final Map<LLBasicBlock, BitMap<String>> exitBitMaps = new HashMap<>();

    final Map<LLDeclaration, List<String>> mapVarToExprs = new HashMap<>();
    final Map<String, LLDeclaration> mapExprToTmp = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    // Initialize all bitmaps
    BitMap<String> defaultBitMap = new BitMap<>();
    initBitMap(controlFlowGraph, mapVarToExprs, defaultBitMap);
    //System.out.println("default bitmap\n");
    //System.out.println(defaultBitMap.toString());
    
    // Initialize the entry block's bit maps
    final BitMap<String> globalEntryInBitMap = new BitMap<>(defaultBitMap);
    final BitMap<String> globalEntryOutBitMap = new BitMap<>(defaultBitMap);
    globalEntryInBitMap.zero();
    globalEntryOutBitMap.zero();
    update(controlFlowGraph.getEntry(), mapVarToExprs, globalEntryInBitMap, globalEntryOutBitMap);
    entryBitMaps.put(controlFlowGraph.getEntry(), globalEntryInBitMap); 
    exitBitMaps.put(controlFlowGraph.getEntry(), globalEntryOutBitMap); 
    workSet.addAll(controlFlowGraph.getEntry().getSuccessors());
    visited.add(controlFlowGraph.getEntry());

    // Initialize rest of bitmaps
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
      if (update(block, mapVarToExprs, entryBitMaps.get(block), exitBitMaps.get(block))) {
        for (LLBasicBlock s : block.getSuccessors()) {
          BitMap<String> entryMap = entryBitMaps.get(s);
          entryMap.and(exitBitMaps.get(block));

          // Add all sucessors to work set
          workSet.add(s);
        }
      }
    }

    // allocate a unique temporary for each expression
    for (String expr : defaultBitMap.getKeySet()) {
      LLDeclaration tmp = methodDeclaration.newAlias();
      mapExprToTmp.put(expr, tmp);
    }

    // DEBUGGING: print available expressions
    /*
    System.out.println("DEBUGGING CSE\n");
    workSet.clear();
    visited.clear();
    workSet.add(controlFlowGraph.getEntry());
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);
      System.out.println(block.prettyString(0));
      System.out.println("Entry bitmap\n");
      System.out.println(entryBitMaps.get(block).toString());
      System.out.println("Exit bitmap\n");
      System.out.println(exitBitMaps.get(block).toString());
      if (!visited.contains(block)) {
        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }
    */

    // TODO: eliminate common sub expressions across blocks
    for (LLBasicBlock block : visited) {
      transform(methodDeclaration, block, entryBitMaps.get(block), exitBitMaps.get(block));
    }
  }

  // Value numbering method
  // once set of available expressions for a method has been determined eliminate common subexpressions
  // TODO:
  //  not yet fully integrated
  //  not yet using values from other blocks
  //  not yet saving values to temporaries seen by the entire method
  //    so can be shared across basic blocks
  public static void transform(LLMethodDeclaration methodDeclaration, LLBasicBlock llBasicBlock, BitMap<String> entryBitMap, BitMap<String> exitBitMap)
  {

    CSETable cseTable = new CSETable(methodDeclaration); 

    List<LLInstruction> newLLInstructions = new ArrayList<>();

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
  }
}
