package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

public class HLBinaryExpression implements HLExpression {

  private final HLExpression left;
  private final BinaryExpressionType type;
  private final HLExpression right;

  public HLBinaryExpression(HLExpression left, BinaryExpressionType type, HLExpression right) {
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
