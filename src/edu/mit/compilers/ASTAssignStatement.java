package edu.mit.compilers;

class ASTAssignStatement implements ASTStatement {

  private final ASTLocationExpression location;
  private final ASTExpression expression;

  public ASTAssignStatement(ASTLocationExpression location, ASTExpression expression) {
    this.location = location;
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
