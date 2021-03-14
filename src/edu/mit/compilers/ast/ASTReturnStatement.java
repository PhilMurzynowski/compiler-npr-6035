package edu.mit.compilers.ast;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTReturnStatement implements ASTStatement {

  private final Optional<ASTExpression> expression;

  private ASTReturnStatement(Optional<ASTExpression> expression) {
    this.expression = expression;
  }

  public static class Builder {

    private Optional<ASTExpression> expression;

    public Builder() {
      expression = Optional.empty();
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = Optional.of(expression);
      return this;
    }

    public ASTReturnStatement build() {
      return new ASTReturnStatement(expression);
    }
  }

	public Optional<ASTExpression> getExpression() {
		return expression;
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
    s.append("return");
    if (expression.isPresent()) {
      s.append(" ");
      s.append(expression.get().prettyString(depth));
    }
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTReturnStatement {\n");
    if (expression.isPresent()) {
      s.append(indent(depth + 1) + "expression: " + expression.get().debugString(depth + 1) + ",\n");
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
