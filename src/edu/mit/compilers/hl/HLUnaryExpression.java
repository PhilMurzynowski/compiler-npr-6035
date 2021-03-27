package edu.mit.compilers.hl;

import edu.mit.compilers.common.*; 

class HLUnaryExpression implements HLExpression {

  private final UnaryExpressionType type;
  private final HLExpression expression;

  public HLUnaryExpression(UnaryExpressionType type, HLExpression expression) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
