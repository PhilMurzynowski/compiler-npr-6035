package edu.mit.compilers.ast;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTForStatement implements ASTStatement {

  private final ASTIDAssignStatement initial;
  private final ASTExpression condition;
  private final ASTCompoundAssignStatement update;
  private final ASTBlock body;

  private ASTForStatement(ASTIDAssignStatement initial, ASTExpression condition, ASTCompoundAssignStatement update, ASTBlock body) {
    this.initial = initial;
    this.condition = condition;
    this.update = update;
    this.body = body;
  }

  public static class Builder {

    private ASTIDAssignStatement initial;
    private ASTExpression condition;
    private ASTCompoundAssignStatement update;
    private ASTBlock body;

    public Builder() {
      initial = null;
      condition = null;
      update = null;
      body = null;
    }

    public Builder withInitial(ASTIDAssignStatement initial) {
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
    s.append("for (");
    s.append(initial.prettyString(depth));
    s.append(" ");
    s.append(condition.prettyString(depth));
    s.append("; ");
    String update = this.update.prettyString(depth);
    s.append(update.substring(0, update.length() - 1));
    s.append(") ");
    s.append(body.prettyString(depth));
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTForStatement {\n");
    s.append(indent(depth + 1) + "initial: " + initial.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "update: " + update.debugString(depth + 1) + ",\n");
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
