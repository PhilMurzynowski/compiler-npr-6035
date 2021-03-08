package edu.mit.compilers;

class ASTMethodCallStatement implements ASTStatement {

  private final ASTMethodCallExpression call;

  public ASTMethodCallStatement(ASTMethodCallExpression call) {
    this.call = call;
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
