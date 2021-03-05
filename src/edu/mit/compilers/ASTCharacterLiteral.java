package edu.mit.compilers;

class ASTCharacterLiteral implements ASTExpression {

  private final char value;

  public ASTCharacterLiteral(char value) {
    this.value = value;
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
