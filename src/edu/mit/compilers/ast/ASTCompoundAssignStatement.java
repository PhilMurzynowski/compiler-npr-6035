package edu.mit.compilers.ast;

import java.util.Optional;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTCompoundAssignStatement implements ASTStatement {

  public enum Type {
    ADD,
    SUBTRACT,
    INCREMENT,
    DECREMENT,
  }

  private final TextLocation textLocation;
  private final ASTLocationExpression location;
  private final Type type;
  private final Optional<ASTExpression> expression;

  private ASTCompoundAssignStatement(TextLocation textLocation, ASTLocationExpression location, Type type, Optional<ASTExpression> expression) {
    this.textLocation = textLocation;
    this.location = location;
    this.type = type;
    this.expression = expression;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private ASTLocationExpression location;
    private Type type;
    private Optional<ASTExpression> expression;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      location = null;
      type = null;
      expression = Optional.empty();
    }

    public Builder withLocation(ASTLocationExpression location) {
      this.location = location;
      return this;
    }

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = Optional.of(expression);
      return this;
    }

    public ASTCompoundAssignStatement build() {
      assert location != null;
      assert type != null;

      return new ASTCompoundAssignStatement(textLocation, location, type, expression);
    }
  }

  public ASTLocationExpression getLocation() {
    return location;
  }

  public Type getType() {
    return type;
  }

  public Optional<ASTExpression> getExpression() {
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
    if (type.equals(Type.ADD)) {
      s.append(" += ");
    } else if (type.equals(Type.SUBTRACT)) {
      s.append(" -= ");
    } else if (type.equals(Type.INCREMENT)) {
      s.append("++");
    } else /* if (type.equals(Type.DECREMENT)) */ {
      s.append("--");
    }
    if (expression.isPresent()) {
      s.append(expression.get().prettyString(depth));
    }
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTCompoundAssignStatement {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
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
