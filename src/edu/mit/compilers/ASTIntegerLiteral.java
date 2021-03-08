package edu.mit.compilers;

class ASTIntegerLiteral implements ASTExpression {

  private final long value;

  public ASTIntegerLiteral(long value) {
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
