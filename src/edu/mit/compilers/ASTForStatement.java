package edu.mit.compilers;

class ASTForStatement implements ASTStatement {

  private final ASTAssignStatement initial;
  private final ASTExpression condition;
  private final ASTCompoundAssignStatement update;
  private final ASTBlock body;

  public ASTForStatement(ASTAssignStatement initial, ASTExpression condition, ASTCompoundAssignStatement update, ASTBlock body) {
    this.initial = initial;
    this.condition = condition;
    this.update = update;
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
