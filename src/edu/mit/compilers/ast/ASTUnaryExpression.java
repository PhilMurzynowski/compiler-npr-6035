package edu.mit.compilers.ast;

import java.util.Stack;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTUnaryExpression implements ASTExpression {

  public enum Type {
    NOT,
    NEGATE,
  }

  private final Type type;
  private final ASTExpression expression;

  private ASTUnaryExpression(Type type, ASTExpression expression) {
    this.type = type;
    this.expression = expression;
  }

  public static class Builder {

    private final Stack<Type> types;
    private ASTExpression expression;

    public Builder() {
      types = new Stack<>();
      expression = null;
    }

    public Builder pushType(Type type) {
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
        expression = new ASTUnaryExpression(types.pop(), expression);
      }

      return expression;
    }

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
    if (type.equals(Type.NOT)) {
      s.append("!");
    } else /* if (type.equals(Type.NEGATE)) */ {
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
