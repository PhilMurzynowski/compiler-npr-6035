package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.*;


public class CommonSubExpression implements Optimization {

  // Perform GEN KILL update for available expressions
  public static boolean update(LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExprs, BitMap<String> entryBitMap, BitMap<String> exitBitMap, List<LLDeclaration> globals) {

    BitMap<String> currentBitMap = new BitMap<>(entryBitMap);
    for (LLInstruction instruction : llBasicBlock.getInstructions()) {


      if (instruction instanceof LLBinary || instruction instanceof LLUnary /* || instruction instanceof LLCompare */) {
        String expr = instruction.getUniqueExpressionString();
        //System.err.println("Block update: encountered expr => " + expr);
        // GEN
        // track all variables used in expr, if change need to KILL expr
        for (LLDeclaration use : instruction.uses()) {
          if (mapVarToExprs.containsKey(use)) {
            List<String> exprs = new ArrayList<>(mapVarToExprs.get(use));
            exprs.add(expr);
            mapVarToExprs.put(use, exprs);
          } else {
            mapVarToExprs.put(use, List.of(expr));
          }
        }
        currentBitMap.set(expr);

      } else if (instruction instanceof LLInternalCall || instruction instanceof LLExternalCall) {

        // Invalidate expressions tied to globals when encountering a function call, as the function call could affect global variables

        for (LLDeclaration global : globals) 
          if( mapVarToExprs.containsKey(global)) {
          // if in the map (used in exprs), clear all connected expressions
          for (String expr : mapVarToExprs.get(global)) {
            currentBitMap.clear(expr);
          } 
        }

      }

      if (instruction.definition().isPresent()) {
        LLDeclaration definition = instruction.definition().get();
        if(mapVarToExprs.containsKey(definition)) {
          for (String expr : mapVarToExprs.get(definition)) {
            // KILL
            //System.err.println("Killing\n");
            //System.err.println(llBasicBlock.prettyString(0));
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

      if (instruction instanceof LLBinary || instruction instanceof LLUnary /* || instruction instanceof LLCompare */) {
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
    //System.err.println("default bitmap\n");
    //System.err.println(defaultBitMap.toString());
    
    // Initialize the entry block's bit maps
    final BitMap<String> globalEntryInBitMap = new BitMap<>(defaultBitMap);
    final BitMap<String> globalEntryOutBitMap = new BitMap<>(defaultBitMap);
    globalEntryInBitMap.zero();
    //globalEntryOutBitMap.zero();
    //update(controlFlowGraph.getEntry(), mapVarToExprs, globalEntryInBitMap, globalEntryOutBitMap);
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
      if (update(block, mapVarToExprs, entryBitMaps.get(block), exitBitMaps.get(block), globals)) {
        for (LLBasicBlock s : block.getSuccessors()) {
          BitMap<String> entryMap = entryBitMaps.get(s);
          //System.err.println("Current block exit bitmap");
          //System.err.println(exitBitMaps.get(block).toString());
          entryMap.and(exitBitMaps.get(block));
          //System.err.println("Successor block exit bitmap");
          //System.err.println(entryMap.toString());

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

    /*
    // DEBUGGING: print available expressions
    System.err.println("DEBUGGING CSE\n");
    workSet.clear();
    visited.clear();
    workSet.add(controlFlowGraph.getEntry());
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);
      System.err.println(block.prettyString(0));
      System.err.println("Entry bitmap\n");
      System.err.println(entryBitMaps.get(block).toString());
      System.err.println("Exit bitmap\n");
      System.err.println(exitBitMaps.get(block).toString());
      if (!visited.contains(block)) {
        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }
    */

    for (LLBasicBlock block : visited) {
      transform(methodDeclaration, block, mapVarToExprs, mapExprToTmp, entryBitMaps.get(block), exitBitMaps.get(block), globals);
    }
  }

  public static void transform(LLMethodDeclaration methodDeclaration, LLBasicBlock llBasicBlock, Map<LLDeclaration, List<String>> mapVarToExprs, Map<String, LLDeclaration> mapExprToTmp, BitMap<String> entryBitMap, BitMap<String> exitBitMap, List<LLDeclaration> globals)
  {

    LocalCSETable localCSETable = new LocalCSETable(methodDeclaration); 
    BitMap<String> currentBitMap = new BitMap<>(entryBitMap);

    List<LLInstruction> newLLInstructions = new ArrayList<>();

    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      StringBuilder valueExprBuilder = new StringBuilder();
      String valueExpr;

      if (instruction instanceof LLBinary binaryInstruction) {

        valueExprBuilder.append(localCSETable.varToVal(binaryInstruction.getLeft()));
        valueExprBuilder.append(binaryInstruction.getType());
        valueExprBuilder.append(localCSETable.varToVal(binaryInstruction.getRight()));
        valueExpr = valueExprBuilder.toString();
        localCSETable.exprToVal(valueExpr);

      } else if (instruction instanceof LLUnary unaryInstruction) {

        UnaryExpressionType type = unaryInstruction.getType();

        if (type == UnaryExpressionType.NOT || type ==  UnaryExpressionType.NEGATE) {
          valueExprBuilder.append(type);
          valueExprBuilder.append(localCSETable.varToVal(unaryInstruction.getExpression()));
        } else if (type == UnaryExpressionType.INCREMENT || type == UnaryExpressionType.DECREMENT) {
          valueExprBuilder.append(localCSETable.varToVal(unaryInstruction.getExpression()));
          valueExprBuilder.append(type);
        } else {
          throw new RuntimeException("unreachable");
        }
        valueExpr = valueExprBuilder.toString();
        localCSETable.exprToVal(valueExpr);
 
      } else if (instruction instanceof LLLoadScalar loadInstruction) {

        newLLInstructions.add(instruction);

        LLDeclaration first = loadInstruction.getDeclaration();
        LLDeclaration second = loadInstruction.getResult();
        localCSETable.varToVal(first);
        localCSETable.copyVarToVal(first, second);
        valueExpr = valueExprBuilder.toString();
        localCSETable.exprToVal(valueExpr);

        // No GEN possible
        // Update Bitmap, KILL as needed
        if (instruction.definition().isPresent()) {
          LLDeclaration definition = instruction.definition().get();
          if(mapVarToExprs.containsKey(definition)) {
            for (String expr : mapVarToExprs.get(definition)) {
              // KILL
              currentBitMap.clear(expr);
            }
          }
        }

        continue;
        
      } else if (instruction instanceof LLStoreScalar storeInstruction) {
        
        newLLInstructions.add(instruction);

        LLDeclaration first = storeInstruction.getExpression();
        LLDeclaration second = storeInstruction.getDeclaration();
        localCSETable.varToVal(first);
        localCSETable.copyVarToVal(first, second);
        valueExpr = valueExprBuilder.toString();
        localCSETable.exprToVal(valueExpr);

        // No GEN possible
        // Update Bitmap, KILL as needed
        if (instruction.definition().isPresent()) {
          LLDeclaration definition = instruction.definition().get();
          if(mapVarToExprs.containsKey(definition)) {
            for (String expr : mapVarToExprs.get(definition)) {
              // KILL
              currentBitMap.clear(expr);
            }
          }
        }

        continue;

      // Arrays? LLCopy?
          
      } else if (instruction instanceof LLInternalCall || instruction instanceof LLExternalCall) {

        newLLInstructions.add(instruction);

        // since function calls can modify global variables, assign each global variable a new value
        for (LLDeclaration global : globals) {
          localCSETable.addVarToVal(global);
          if(mapVarToExprs.containsKey(global)) {
            for (String expr : mapVarToExprs.get(global)) {
              // KILL
              currentBitMap.clear(expr);
            }
          }
        }

        continue;
      
      } else {

        newLLInstructions.add(instruction);

        // No GEN possible
        // Update Bitmap, KILL as needed
        if (instruction.definition().isPresent()) {
          LLDeclaration definition = instruction.definition().get();
          if(mapVarToExprs.containsKey(definition)) {
            for (String expr : mapVarToExprs.get(definition)) {
              // KILL
              currentBitMap.clear(expr);
            }
          }
        }

        continue;

      }

      // each expression has two string representations
      //  one for the local basic block based on value numbering
      //  one for the global control flow graph bitmap
      String globalExpr = instruction.getUniqueExpressionString();
      //System.err.println("globalExpr: " + globalExpr);
      //System.err.println("currentBitMap.get(globalExpr) " + currentBitMap.get(globalExpr));
      //System.err.println("localCSETable.inExprToTmp(valueExpr) " + localCSETable.inExprToTmp(valueExpr));


      if (currentBitMap.get(globalExpr) && !localCSETable.inExprToTmp(valueExpr)) {
        
        // Expression available from previous block, use result from global temporary
        //System.err.println("using global for: " + globalExpr);

        LLDeclaration globalTmp = mapExprToTmp.get(globalExpr);
        LLCopy globalCopyInstruction = new LLCopy(globalTmp, instruction.definition().get());
        newLLInstructions.add(globalCopyInstruction);

      } else if (!localCSETable.inExprToTmp(valueExpr)) {

        // Evaluate instruction and copy result into local temporary 
        //System.err.println("copying to local: " + valueExpr + ", copying to global: " + globalExpr);

        LLDeclaration localTmp = localCSETable.addExprToTmp(valueExpr);
        newLLInstructions.add(instruction);
        // copy into local tmp
        LLCopy localCopyInstruction = new LLCopy(instruction.definition().get(), localTmp);
        newLLInstructions.add(localCopyInstruction);
        // copy into global tmp
        LLDeclaration globalTmp = mapExprToTmp.get(globalExpr);
        LLCopy globalCopyInstruction = new LLCopy(localTmp, globalTmp);
        newLLInstructions.add(globalCopyInstruction);

      } else {

        // Expression available in local temporary
        //System.err.println("using local for: " + valueExpr);

        LLDeclaration localTmp = localCSETable.getExprToTmp(valueExpr);
        // modified instruction as no longer using LLBinary, LLUnary, etc, just the tmp
        LLCopy modifiedInstruction = new LLCopy(localTmp, instruction.definition().get());
        newLLInstructions.add(modifiedInstruction);
      }

      // GEN
      //System.err.println("GEN: " + globalExpr);
      currentBitMap.set(globalExpr);

      // Update Bitmap, KILL as needed
      LLDeclaration definition = instruction.definition().get();
      if(mapVarToExprs.containsKey(definition)) {
        for (String expr : mapVarToExprs.get(definition)) {
          // KILL
          //System.err.println("KILL: " + expr);
          currentBitMap.clear(expr);
        }
      }
    }

    llBasicBlock.setInstructions(newLLInstructions);

    assert currentBitMap.sameValue(exitBitMap);
  }
}
