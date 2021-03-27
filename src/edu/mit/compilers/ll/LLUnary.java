package edu.mit.compilers.ll;

import edu.mit.compilers.common.UnaryExpressionType;

public class LLUnary implements LLInstruction {

  private final UnaryExpressionType type;
  private final LLDeclaration expression;
  private final LLDeclaration result;

  public LLUnary(UnaryExpressionType type, LLDeclaration expression, LLDeclaration result) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
