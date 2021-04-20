package edu.mit.compilers.ll;

import java.util.Map;

import edu.mit.compilers.hl.*;
import edu.mit.compilers.common.*;

public class LLShortCircuit {

  // DONE: Phil
  public static LLBasicBlock shortExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      return LLShortCircuit.shortBinaryExpression(binaryExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    } else if (expression instanceof HLUnaryExpression unaryExpression) {
      return LLShortCircuit.shortUnaryExpression(unaryExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    } else if (expression instanceof HLLoadScalarExpression loadScalarExpression) {
      return LLShortCircuit.shortLoadScalarExpression(loadScalarExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    } else if (expression instanceof HLLoadArrayExpression loadArrayExpression) {
      return LLShortCircuit.shortLoadArrayExpression(loadArrayExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    } else if (expression instanceof HLCallExpression callExpression) {
      return LLShortCircuit.shortCallExpression(callExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    //} else if (expression instanceof HLLengthExpression lengthExpression) {
    //  return LLShortCircuit.shortLengthExpression(lengthExpression, methodDeclaration, trueTarget, falseTarget);
    } else if (expression instanceof HLIntegerLiteral integerLiteral) {
      return LLShortCircuit.shortIntegerLiteral(integerLiteral, methodDeclaration, trueTarget, falseTarget);
    } else {
      throw new RuntimeException("unreachable");
    }

  }

  // DONE: Robert
  public static LLBasicBlock shortBinaryExpression(HLBinaryExpression binaryExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (binaryExpression.getType() == BinaryExpressionType.OR) {
      LLBasicBlock rightBB = shortExpression(binaryExpression.getRight(), methodDeclaration, trueTarget, falseTarget, argumentAliases);
      return shortExpression(binaryExpression.getLeft(), methodDeclaration, trueTarget, rightBB, argumentAliases);
    } else if (binaryExpression.getType() == BinaryExpressionType.AND) {
      LLBasicBlock rightBB = shortExpression(binaryExpression.getRight(), methodDeclaration, trueTarget, falseTarget, argumentAliases);
      return shortExpression(binaryExpression.getLeft(), methodDeclaration, rightBB, falseTarget, argumentAliases);
    } else if (binaryExpression.getType() == BinaryExpressionType.EQUAL
        || binaryExpression.getType() == BinaryExpressionType.NOT_EQUAL
        || binaryExpression.getType() == BinaryExpressionType.LESS_THAN
        || binaryExpression.getType() == BinaryExpressionType.LESS_THAN_OR_EQUAL
        || binaryExpression.getType() == BinaryExpressionType.GREATER_THAN
        || binaryExpression.getType() == BinaryExpressionType.GREATER_THAN_OR_EQUAL) {
      LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

      final LLAliasDeclaration leftResult = methodDeclaration.newAlias();
      final LLControlFlowGraph leftCFG = LLBuilder.buildExpression(binaryExpression.getLeft(), methodDeclaration, leftResult, argumentAliases);
      resultCFG = resultCFG.concatenate(leftCFG);

      final LLAliasDeclaration rightResult = methodDeclaration.newAlias();
      final LLControlFlowGraph rightCFG = LLBuilder.buildExpression(binaryExpression.getRight(), methodDeclaration, rightResult, argumentAliases);
      resultCFG = resultCFG.concatenate(rightCFG);

      final LLBasicBlock compareBB = new LLBasicBlock(
        new LLCompare(leftResult, binaryExpression.getType().toComparisonType(), rightResult)
      );

      LLBasicBlock.setTrueTarget(compareBB, trueTarget);
      LLBasicBlock.setFalseTarget(compareBB, falseTarget);

      resultCFG = resultCFG.concatenate(compareBB);

      return resultCFG.getEntry();
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Noah
  public static LLBasicBlock shortUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (unaryExpression.getType().equals(UnaryExpressionType.NOT)) {
      return shortExpression(unaryExpression.getExpression(), methodDeclaration, falseTarget, trueTarget, argumentAliases);
    } else {
      throw new RuntimeException("cannot short-circuit unary of type " + unaryExpression.getType().name());
    }

  }

  // DONE: Phil
  public static LLBasicBlock shortLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration loadResult = methodDeclaration.newAlias();
    final LLControlFlowGraph loadCFG = LLBuilder.buildLoadScalarExpression(loadScalarExpression, loadResult, argumentAliases);
    resultCFG = resultCFG.concatenate(loadCFG);

    final LLBasicBlock compareBB = new LLBasicBlock(
      new LLCompare(loadResult, ComparisonType.EQUAL, new LLConstantDeclaration(1))
    );

    LLBasicBlock.setTrueTarget(compareBB, trueTarget);
    LLBasicBlock.setFalseTarget(compareBB, falseTarget);

    resultCFG = resultCFG.concatenate(compareBB);

    return resultCFG.getEntry();
  }

  // DONE: Robert
  public static LLBasicBlock shortLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration loadResult = methodDeclaration.newAlias();
    final LLControlFlowGraph loadCFG = LLBuilder.buildLoadArrayExpression(loadArrayExpression, methodDeclaration, loadResult, argumentAliases);
    resultCFG = resultCFG.concatenate(loadCFG);

    final LLBasicBlock compareBB = new LLBasicBlock(
      new LLCompare(loadResult, ComparisonType.EQUAL, new LLConstantDeclaration(1))
    );

    LLBasicBlock.setTrueTarget(compareBB, trueTarget);
    LLBasicBlock.setFalseTarget(compareBB, falseTarget);

    resultCFG = resultCFG.concatenate(compareBB);

    return resultCFG.getEntry();
  }

  // DONE: Noah
  public static LLBasicBlock shortCallExpression(HLCallExpression callExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      return shortInternalCallExpression(internalCallExpression, methodDeclaration, trueTarget, falseTarget, argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Phil
  public static LLBasicBlock shortInternalCallExpression(HLInternalCallExpression internalCallExpression, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration callResult = methodDeclaration.newAlias();
    final LLControlFlowGraph callCFG = LLBuilder.buildInternalCallExpression(internalCallExpression, methodDeclaration, callResult, argumentAliases);
    resultCFG = resultCFG.concatenate(callCFG);

    final LLBasicBlock compareBB = new LLBasicBlock(
      new LLCompare(callResult, ComparisonType.EQUAL, new LLConstantDeclaration(1))
    );

    LLBasicBlock.setTrueTarget(compareBB, trueTarget);
    LLBasicBlock.setFalseTarget(compareBB, falseTarget);

    resultCFG = resultCFG.concatenate(compareBB);

    return resultCFG.getEntry();
  }

  // DONE: Robert
  public static LLBasicBlock shortIntegerLiteral(HLIntegerLiteral integerLiteral, LLMethodDeclaration methodDeclaration, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration integerResult = methodDeclaration.newAlias();
    final LLControlFlowGraph integerCFG = LLBuilder.buildIntegerLiteral(integerLiteral, methodDeclaration, integerResult);
    resultCFG = resultCFG.concatenate(integerCFG);

    final LLBasicBlock compareBB = new LLBasicBlock(
      new LLCompare(integerResult, ComparisonType.EQUAL, new LLConstantDeclaration(1))
    );

    LLBasicBlock.setTrueTarget(compareBB, trueTarget);
    LLBasicBlock.setFalseTarget(compareBB, falseTarget);

    resultCFG = resultCFG.concatenate(compareBB);

    return resultCFG.getEntry();
  }

}
