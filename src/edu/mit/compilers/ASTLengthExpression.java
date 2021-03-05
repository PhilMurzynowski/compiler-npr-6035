package edu.mit.compilers;

class ASTLengthExpression implements ASTExpression {

  private final String identifier;

  public ASTLengthExpression(String identifier) {
    this.identifier = identifier;
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
