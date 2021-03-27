package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

class HLBinaryExpression implements HLExpression {

  private final HLExpression left;
  private final BinaryExpressionType type;
  private final HLExpression right;

  public HLBinaryExpression(HLExpression left, BinaryExpressionType type, HLExpression right) {
    throw new RuntimeException("not implemented");
  }

}
