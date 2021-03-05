package edu.mit.compilers;

import java.util.Optional;

class ASTReturnStatement implements ASTStatement {

  private final Optional<ASTExpression> expression;

  public ASTReturnStatement(Optional<ASTExpression> expression) {
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
