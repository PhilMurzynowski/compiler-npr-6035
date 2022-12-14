package edu.mit.compilers.ast;

import java.util.Optional;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTIfStatement implements ASTStatement {

  private final TextLocation textLocation;
  private final ASTExpression condition;
  private final ASTBlock body;
  private final Optional<ASTBlock> other;

  private ASTIfStatement(TextLocation textLocation, ASTExpression condition, ASTBlock body, Optional<ASTBlock> other) {
    this.textLocation = textLocation;
    this.condition = condition;
    this.body = body;
    this.other = other;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private ASTExpression condition;
    private ASTBlock body;
    private Optional<ASTBlock> other;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
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

      return new ASTIfStatement(textLocation, condition, body, other);
    }
  }

	public ASTExpression getCondition() {
		return condition;
	}

  public ASTBlock getBody() {
    return body;
  }

  public Optional<ASTBlock> getOther() {
    return other;
  }

  @Override
  public TextLocation getTextLocation() {
    return textLocation;
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
    s.append("if (");
    s.append(condition.prettyString(depth));
    s.append(") ");
    s.append(body.prettyString(depth));
    if (other.isPresent()) {
      s.append(" else ");
      s.append(other.get().prettyString(depth));
    }
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTIfStatement {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    if (other.isPresent()) {
      s.append(indent(depth + 1) + "other: " + other.get().debugString(depth + 1) + ",\n");
    }
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
