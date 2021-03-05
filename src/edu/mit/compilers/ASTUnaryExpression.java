package edu.mit.compilers;

class ASTUnaryExpression implements ASTExpression {

  public enum Type {
    NOT,
    NEGATE,
  }

  private final Type type;
  private final ASTExpression expression;

  public ASTUnaryExpression(Type type, ASTExpression expression) {
    this.type = type;
    this.expression = expression;
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
