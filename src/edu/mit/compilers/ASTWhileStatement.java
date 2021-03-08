package edu.mit.compilers;

class ASTWhileStatement implements ASTStatement {

  private final ASTExpression condition;
  private final ASTBlock body;

  private ASTWhileStatement(ASTExpression condition, ASTBlock body) {
    this.condition = condition;
    this.body = body;
  }

  public static class Builder {

    private ASTExpression condition;
    private ASTBlock body;

    public Builder() {
      condition = null;
      body = null;
    }

    public Builder withCondition(ASTExpression condition) {
      this.condition = condition;
      return this;
    }

    public Builder withBody(ASTBlock body) {
      this.body = body;
      return this;
    }

    public ASTWhileStatement build() {
      assert condition != null;
      assert body != null;

      return new ASTWhileStatement(condition, body);
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
