package edu.mit.compilers;

import java.util.Optional;

class ASTIfStatement implements ASTStatement {

  private final ASTExpression condition;
  private final ASTBlock body;
  private final Optional<ASTBlock> other;

  private ASTIfStatement(ASTExpression condition, ASTBlock body, Optional<ASTBlock> other) {
    this.condition = condition;
    this.body = body;
    this.other = other;
  }

  public static class Builder {

    private ASTExpression condition;
    private ASTBlock body;
    private Optional<ASTBlock> other;

    public Builder() {
      condition = null;
      body = null;
      other = Optional.empty();
    }

    public Builder withCondition(ASTExpression condition) {
      this.condition = condition;
      return this;
    }

    public Builder withBody(ASTBlock body) {
      this.body = body;
      return this;
    }

    public Builder withOther(ASTBlock other) {
      this.other = Optional.of(other);
      return this;
    }

    public ASTIfStatement build() {
      assert condition != null;
      assert body != null;

      return new ASTIfStatement(condition, body, other);
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
