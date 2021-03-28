package edu.mit.compilers.hl;

import edu.mit.compilers.common.*; 

import static edu.mit.compilers.common.Utilities.indent;

public class HLUnaryExpression implements HLExpression {

  private final UnaryExpressionType type;
  private final HLExpression expression;

  public HLUnaryExpression(UnaryExpressionType type, HLExpression expression) {
    this.type = type;
    this.expression = expression;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLUnaryExpression {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
