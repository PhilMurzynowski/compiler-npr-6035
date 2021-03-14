package edu.mit.compilers.ast;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTWhileStatement implements ASTStatement {

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

  public ASTExpression getCondition() {
    return condition;
  }

  public ASTBlock getBody() {
    return body;
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTStatement.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("while (");
    s.append(condition.prettyString(depth));
    s.append(") ");
    s.append(body.prettyString(depth));
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTWhileStatement {\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
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
