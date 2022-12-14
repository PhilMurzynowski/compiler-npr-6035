package edu.mit.compilers.ast;

import java.util.Stack;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTUnaryExpression implements ASTExpression {

  private final TextLocation textLocation;
  private final UnaryExpressionType type;
  private final ASTExpression expression;

  private ASTUnaryExpression(TextLocation textLocation, UnaryExpressionType type, ASTExpression expression) {
    this.textLocation = textLocation;
    this.type = type;
    this.expression = expression;
  }

  public static class Builder {

    private final Stack<TextLocation> textLocations;
    private final Stack<UnaryExpressionType> types;
    private ASTExpression expression;

    public Builder() {
      textLocations = new Stack<>();
      types = new Stack<>();
      expression = null;
    }

    public Builder pushType(TextLocation textLocation, UnaryExpressionType type) {
      textLocations.push(textLocation);
      types.push(type);
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public ASTExpression build() {
      assert expression != null;

      while (!types.isEmpty()) {
        expression = new ASTUnaryExpression(textLocations.pop(), types.pop(), expression);
      }

      return expression;
    }

  }

  public UnaryExpressionType getType() {
    return type;
  }

  public ASTExpression getExpression() {
    return expression;
  }

  private boolean acceptsInteger() {
    return type.equals(UnaryExpressionType.NEGATE);
  }

  private boolean acceptsBoolean() {
    return type.equals(UnaryExpressionType.NOT);
  }

  public boolean acceptsType(VariableType type) {
    if (type.equals(VariableType.INTEGER)) {
      return acceptsInteger();
    } else /* if (type.equals(VariableType.BOOLEAN)) */ {
      return acceptsBoolean();
    }
  }

  private boolean returnsInteger() {
    return type.equals(UnaryExpressionType.NEGATE);
  }

  @SuppressWarnings("unused")
  private boolean returnsBoolean() {
    return type.equals(UnaryExpressionType.NOT);
  }

  public VariableType returnType() {
    if (returnsInteger()) {
      return VariableType.INTEGER;
    } else /* if (returnsBoolean()) */ {
      return VariableType.BOOLEAN;
    }
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
  public <T> T accept(ASTArgument.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTExpression.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("(");
    if (type.equals(UnaryExpressionType.NOT)) {
      s.append("!");
    } else /* if (type.equals(UnaryExpressionType.NEGATE)) */ {
      s.append("-");
    }
    s.append(expression.prettyString(depth));
    s.append(")");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTUnaryExpression {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
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
