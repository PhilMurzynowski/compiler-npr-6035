package edu.mit.compilers;

import java.util.Optional;

class ASTIfStatement implements ASTStatement {

  private final ASTExpression condition;
  private final ASTBlock body;
  private final Optional<ASTBlock> other;

  public ASTIfStatement(ASTExpression condition, ASTBlock body, Optional<ASTBlock> other) {
    this.condition = condition;
    this.body = body;
    this.other = other;
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
