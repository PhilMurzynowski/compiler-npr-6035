package edu.mit.compilers;

class ASTBinaryExpression implements ASTExpression {

  public enum Type {
    OR,
    AND,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULUS,
  }

  private final ASTExpression left;
  private final Type type;
  private final ASTExpression right;

  private ASTBinaryExpression(ASTExpression left, Type type, ASTExpression right) {
    this.left = left;
    this.type = type;
    this.right = right;
  }

  public static class Builder {

    private ASTExpression expression;

    public Builder() {
      expression = null;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public Builder withExpression(Type type, ASTExpression expression) {
      assert this.expression != null;

      this.expression = new ASTBinaryExpression(this.expression, type, expression);
      return this;
    }

    public ASTExpression build() {
      assert expression != null;

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
