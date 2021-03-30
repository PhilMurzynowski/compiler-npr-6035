package edu.mit.compilers.ll;

import edu.mit.compilers.common.UnaryExpressionType;

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
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
