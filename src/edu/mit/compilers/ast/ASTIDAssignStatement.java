package edu.mit.compilers.ast;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTIDAssignStatement implements ASTStatement {

  private final TextLocation textLocation;
  private final String identifier;
  private final ASTExpression expression;

  private ASTIDAssignStatement(TextLocation textLocation, String identifier, ASTExpression expression) {
    this.textLocation = textLocation;
    this.identifier = identifier;
    this.expression = expression;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private String identifier;
    private ASTExpression expression;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      identifier = null;
      expression = null;
    }

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public ASTIDAssignStatement build() {
      assert identifier != null;
      assert expression != null;

      return new ASTIDAssignStatement(textLocation, identifier, expression);
    }
  }

  public String getIdentifier() {
    return identifier;
  }

  public ASTExpression getExpression() {
    return expression;
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
    s.append(identifier);
    s.append(" = ");
    s.append(expression.prettyString(depth));
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTIDAssignStatement {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
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
