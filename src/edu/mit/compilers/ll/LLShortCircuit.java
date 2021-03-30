package edu.mit.compilers.ll;

import edu.mit.compilers.hl.*;

public class LLShortCircuit {

  // TODO: Phil
  public static LLBasicBlock shortExpression(HLExpression expression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLBasicBlock shortBinaryExpression(HLBinaryExpression binaryExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    // NOTE(rbd): assert AND or OR binary expression
    // NOTE(rbd): right to left
    // NOTE(rbd): all basic blocks should end with a comparison binary expression
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static LLBasicBlock shortUnaryExpression(HLUnaryExpression unaryExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    // NOTE(rbd): assert NOT unary expression
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLBasicBlock shortLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLBasicBlock shortLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static LLBasicBlock shortCallExpression(HLCallExpression callExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLBasicBlock shortInternalCallExpression(HLInternalCallExpression internalCallExpression, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLBasicBlock shortIntegerLiteral(HLIntegerLiteral integerLiteral, LLBasicBlock trueTarget, LLBasicBlock falseTarget) {
    throw new RuntimeException("not implemented");
  }

}
