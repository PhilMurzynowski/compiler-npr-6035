package edu.mit.compilers;

import java.util.Optional;

class ASTReturnStatement implements ASTStatement {

  private final Optional<ASTExpression> expression;

  private ASTReturnStatement(Optional<ASTExpression> expression) {
    this.expression = expression;
  }

  public static class Builder {

    private Optional<ASTExpression> expression;

    public Builder() {
      expression = Optional.empty();
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = Optional.of(expression);
      return this;
    }

    public ASTReturnStatement build() {
      return new ASTReturnStatement(expression);
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
