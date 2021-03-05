package edu.mit.compilers;

class ASTWhileStatement implements ASTStatement {

  private final ASTExpression condition;
  private final ASTBlock body;

  public ASTWhileStatement(ASTExpression condition, ASTBlock body) {
    this.condition = condition;
    this.body = body;
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
