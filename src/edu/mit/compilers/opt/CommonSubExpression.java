package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;
import java.util.Map;
import java.util.HashMap;

import java.util.*;

public class CommonSubExpression implements Optimization {


  public static Set<LLBasicBlock> apply(CSETable cseTable, LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entryBitMap, BitMap<LLDeclaration> exitBitMap)
  {

    List<LLInstruction> newLLInstructions = new ArrayList<>();
    exitBitMap.subsume(entryBitMap);
    boolean changed = false;

    for (LLInstruction instruction : llBasicBlock.getInstructions()) {

      newLLInstructions.add(instruction);
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
        exprBuilder.append(BinaryExpressionType.EQUAL);
        exprBuilder.append(cseTable.varToVal(cmpInstruction.getRight()));

      } else {

        continue;

      }

      String expr = exprBuilder.toString();
      cseTable.exprToVal(expr);

      if (cseTable.inExprToTmp(expr)) {
        LLDeclaration tmp = cseTable.addExprToTmp(expr);
        LLCopy copyInstruction = new LLCopy(instruction.definition().get(), tmp);
        newLLInstructions.add(copyInstruction);
      }

    }

    return changed ? llBasicBlock.getSuccessors() : new HashSet<>();
  }

  // NOTE(phil): pass in method declaration to create new aliases
  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    throw new RuntimeException("not implemented");
  }

}
