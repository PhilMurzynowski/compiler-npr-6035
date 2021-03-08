package edu.mit.compilers;

import java.util.List;

class ASTMethodCallExpression implements ASTExpression {

  private final String identifier;
  private final List<ASTArgument> arguments;

  public ASTMethodCallExpression(String identifier, List<ASTArgument> arguments) {
    this.identifier = identifier;
    this.arguments = arguments;
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
