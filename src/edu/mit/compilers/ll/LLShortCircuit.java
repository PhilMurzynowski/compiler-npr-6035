package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;

import edu.mit.compilers.hl.*;
import edu.mit.compilers.common.*;

public class LLShortCircuit {

  // TODO: Phil
  public static LLBasicBlock shortExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static LLBasicBlock shortBinaryExpression(HLBinaryExpression binaryExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    if (binaryExpression.getType() == BinaryExpressionType.OR) {
      LLBasicBlock rightBB = shortExpression(binaryExpression.getRight(), methodDeclaration, trueTarget, falseTarget);
      return shortExpression(binaryExpression.getLeft(), methodDeclaration, trueTarget, rightBB);
    } else if (binaryExpression.getType() == BinaryExpressionType.AND) {
      LLBasicBlock rightBB = shortExpression(binaryExpression.getRight(), methodDeclaration, trueTarget, falseTarget);
      return shortExpression(binaryExpression.getLeft(), methodDeclaration, rightBB, falseTarget);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Noah
  public static LLBasicBlock shortUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    if (unaryExpression.getType().equals(UnaryExpressionType.NOT)) {
      return shortExpression(unaryExpression.getExpression(), methodDeclaration, trueTarget, falseTarget);
    } else {
      throw new RuntimeException("cannot short-circuit unary of type " + unaryExpression.getType().name());
    }

  }

  // TODO: Phil
  public static LLBasicBlock shortLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static LLBasicBlock shortLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration zeroResult = methodDeclaration.newAlias();
    resultCFG = resultCFG.concatenate(
      new LLIntegerLiteral(0, zeroResult)
    );

    final LLAliasDeclaration loadResult = methodDeclaration.newAlias();
    final LLControlFlowGraph loadCFG = LLBuilder.buildLoadArrayExpression(loadArrayExpression, methodDeclaration, loadResult);
    resultCFG = resultCFG.concatenate(loadCFG);

    resultCFG = resultCFG.concatenate(
      new LLBasicBlock(
        List.of(
          new LLCompare(zeroResult, loadResult)
        ),
        Optional.of(trueTarget),
        Optional.of(falseTarget)
      )
    );

    return resultCFG.getEntry();
  }

  // DONE: Noah
  public static LLBasicBlock shortCallExpression(HLCallExpression callExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      return shortInternalCallExpression(internalCallExpression, methodDeclaration, trueTarget, falseTarget);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // TODO: Phil
  public static LLBasicBlock shortInternalCallExpression(HLInternalCallExpression internalCallExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static LLBasicBlock shortIntegerLiteral(HLIntegerLiteral integerLiteral, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration zeroResult = methodDeclaration.newAlias();
    resultCFG = resultCFG.concatenate(
      new LLIntegerLiteral(0, zeroResult)
    );

    final LLAliasDeclaration integerResult = methodDeclaration.newAlias();
    final LLControlFlowGraph integerCFG = LLBuilder.buildIntegerLiteral(integerLiteral, methodDeclaration, integerResult);
    resultCFG = resultCFG.concatenate(integerCFG);

    resultCFG = resultCFG.concatenate(
      new LLBasicBlock(
        List.of(
          new LLCompare(zeroResult, integerResult)
        ),
        Optional.of(trueTarget), 
        Optional.of(falseTarget)
      )
    );

    return resultCFG.getEntry();
  }

}
