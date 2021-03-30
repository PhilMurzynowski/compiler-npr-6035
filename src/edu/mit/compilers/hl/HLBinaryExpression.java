package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLBinaryExpression implements HLExpression {

  private final HLExpression left;
  private final BinaryExpressionType type;
  private final HLExpression right;

  public HLBinaryExpression(HLExpression left, BinaryExpressionType type, HLExpression right) {
    this.left = left;
    this.type = type;
    this.right = right;
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
    StringBuilder s = new StringBuilder();
    s.append("HLBinaryExpression {\n");
    s.append(indent(depth + 1) + "left: " + left.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "right: " + right.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
