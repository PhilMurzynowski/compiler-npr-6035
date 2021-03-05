package edu.mit.compilers;

import java.util.Optional;

class ASTLocationExpression implements ASTExpression {

  private final String identifier;
  private final Optional<ASTExpression> offset;

  public ASTLocationExpression(String identifier, Optional<ASTExpression> offset) {
    this.identifier = identifier;
    this.offset = offset;
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
