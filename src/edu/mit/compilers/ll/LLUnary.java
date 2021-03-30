package edu.mit.compilers.ll;

import edu.mit.compilers.common.UnaryExpressionType;

import static edu.mit.compilers.common.Utilities.indent;

public class LLUnary implements LLInstruction {

  private final UnaryExpressionType type;
  private final LLDeclaration expression;
  private final LLDeclaration result;

  public LLUnary(UnaryExpressionType type, LLDeclaration expression, LLDeclaration result) {
    this.type = type;
    this.expression = expression;
    this.result = result;
  }

  public UnaryExpressionType getType() {
    return type;
  }

  public LLDeclaration getExpression() {
    return expression;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLUnary {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
