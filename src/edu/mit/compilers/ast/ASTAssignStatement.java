package edu.mit.compilers.ast;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTAssignStatement implements ASTStatement {

  private final TextLocation textLocation;
  private final ASTLocationExpression location;
  private final ASTExpression expression;

  private ASTAssignStatement(TextLocation textLocation, ASTLocationExpression location, ASTExpression expression) {
    this.textLocation = textLocation;
    this.location = location;
    this.expression = expression;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private ASTLocationExpression location;
    private ASTExpression expression;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      location = null;
      expression = null;
    }

    public Builder withLocation(ASTLocationExpression location) {
      this.location = location;
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public ASTAssignStatement build() {
      assert location != null;
      assert expression != null;

      return new ASTAssignStatement(textLocation, location, expression);
    }
  }

  public ASTLocationExpression getLocation() {
    return location;
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
    s.append(location.prettyString(depth));
    s.append(" = ");
    s.append(expression.prettyString(depth));
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTAssignStatement {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
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
