package edu.mit.compilers;

import java.util.Stack;

class ASTUnaryExpression implements ASTExpression {

  public enum Type {
    NOT,
    NEGATE,
  }

  private final Type type;
  private final ASTExpression expression;

  private ASTUnaryExpression(Type type, ASTExpression expression) {
    this.type = type;
    this.expression = expression;
  }

  public static class Builder {

    private final Stack<Type> types;
    private ASTExpression expression;

    public Builder() {
      types = new Stack<>();
      expression = null;
    }

    public Builder pushType(Type type) {
      types.push(type);
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public ASTExpression build() {
      assert expression != null;

      while (!types.isEmpty()) {
        expression = new ASTUnaryExpression(types.pop(), expression);
      }

      return expression;
    }

  }

  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public boolean equals(Object that) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int hashCode() {
    throw new RuntimeException("not implemented");
  }

}
