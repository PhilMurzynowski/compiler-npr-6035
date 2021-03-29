package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

public class HLBinaryExpression implements HLExpression {

  private final HLExpression left;
  private final BinaryExpressionType type;
  private final HLExpression right;

  public HLBinaryExpression(HLExpression left, BinaryExpressionType type, HLExpression right) {
    this.left = left;
    this.type = type;
    this.right = type;
  }

  public HLExpression getLeft() {
    return this.left;
  }

  public BinaryExpressionType getType() {
    return this.type;
  }

  public HLExpression getRight() {
    return this.right;
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
