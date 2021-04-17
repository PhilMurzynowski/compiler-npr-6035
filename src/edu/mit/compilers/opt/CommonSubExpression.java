package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.*;

public class CommonSubExpression implements Optimization {


  public static boolean update(LLMethodDeclaration methodDeclaration, LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap)
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

  // NOTE(phil): pass in method declaration to create new aliases
  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
  }

}
