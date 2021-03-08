package edu.mit.compilers;

class ASTForStatement implements ASTStatement {

  private final ASTAssignStatement initial;
  private final ASTExpression condition;
  private final ASTCompoundAssignStatement update;
  private final ASTBlock body;

  private ASTForStatement(ASTAssignStatement initial, ASTExpression condition, ASTCompoundAssignStatement update, ASTBlock body) {
    this.initial = initial;
    this.condition = condition;
    this.update = update;
    this.body = body;
  }

  public static class Builder {

    private ASTAssignStatement initial;
    private ASTExpression condition;
    private ASTCompoundAssignStatement update;
    private ASTBlock body;

    public Builder() {
      initial = null;
      condition = null;
      update = null;
      body = null;
    }

    public Builder withInitial(ASTAssignStatement initial) {
      this.initial = initial;
      return this;
    }

    public Builder withCondition(ASTExpression condition) {
      this.condition = condition;
      return this;
    }

    public Builder withUpdate(ASTCompoundAssignStatement update) {
      this.update = update;
      return this;
    }

    public Builder withBody(ASTBlock body) {
      this.body = body;
      return this;
    }

    public ASTForStatement build() {
      assert initial != null;
      assert condition != null;
      assert update != null;
      assert body != null;

      return new ASTForStatement(initial, condition, update, body);
    }
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
