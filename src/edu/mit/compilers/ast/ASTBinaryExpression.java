package edu.mit.compilers.ast;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTBinaryExpression implements ASTExpression {

  public enum Type {
    OR,
    AND,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULUS,
  }

  private final ASTExpression left;
  private final Type type;
  private final ASTExpression right;

  private ASTBinaryExpression(ASTExpression left, Type type, ASTExpression right) {
    this.left = left;
    this.type = type;
    this.right = right;
  }

  public static class Builder {

    private ASTExpression expression;

    public Builder() {
      expression = null;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public Builder withExpression(Type type, ASTExpression expression) {
      assert this.expression != null;

      this.expression = new ASTBinaryExpression(this.expression, type, expression);
      return this;
    }

    public ASTExpression build() {
      assert expression != null;

      return expression;
    }
  }

  public ASTExpression getleft() {
	  return left;
  }

  public ASTExpression getright() {
	  return right;
  }

  public Type getType() {
	  return type;
  }

  private boolean acceptsInteger() {
    return (type.equals(Type.EQUAL))
      || (type.equals(Type.NOT_EQUAL))
      || (type.equals(Type.LESS_THAN))
      || (type.equals(Type.LESS_THAN_OR_EQUAL))
      || (type.equals(Type.GREATER_THAN))
      || (type.equals(Type.GREATER_THAN_OR_EQUAL))
      || (type.equals(Type.ADD))
      || (type.equals(Type.SUBTRACT))
      || (type.equals(Type.MULTIPLY))
      || (type.equals(Type.DIVIDE))
      || (type.equals(Type.MODULUS));
  }

  private boolean acceptsBoolean() {
    return (type.equals(Type.OR))
      || (type.equals(Type.AND))
      || (type.equals(Type.EQUAL))
      || (type.equals(Type.NOT_EQUAL));
  }

  public boolean acceptsType(VariableType type) {
    if (type.equals(VariableType.INTEGER)) {
      return acceptsInteger();
    } else /* if (type.equals(VariableType.BOOLEAN)) */ {
      return acceptsBoolean();
    }
  }

  private boolean returnsInteger() {
    return (type.equals(Type.ADD))
      || (type.equals(Type.SUBTRACT))
      || (type.equals(Type.MULTIPLY))
      || (type.equals(Type.DIVIDE))
      || (type.equals(Type.MODULUS));
  }

  @SuppressWarnings("unused")
  private boolean returnsBoolean() {
    return (type.equals(Type.OR))
      || (type.equals(Type.AND))
      || (type.equals(Type.EQUAL))
      || (type.equals(Type.NOT_EQUAL))
      || (type.equals(Type.LESS_THAN))
      || (type.equals(Type.LESS_THAN_OR_EQUAL))
      || (type.equals(Type.GREATER_THAN))
      || (type.equals(Type.GREATER_THAN_OR_EQUAL));
  }

  public VariableType returnType() {
    if (returnsInteger()) {
      return VariableType.INTEGER;
    } else /* if (returnsBoolean()) */ {
      return VariableType.BOOLEAN;
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
    s.append(left.prettyString(depth));
    if (type.equals(Type.OR)) {
      s.append(" || ");
    } else if (type.equals(Type.AND)) {
      s.append(" && ");
    } else if (type.equals(Type.EQUAL)) {
      s.append(" == ");
    } else if (type.equals(Type.NOT_EQUAL)) {
      s.append(" != ");
    } else if (type.equals(Type.LESS_THAN)) {
      s.append(" < ");
    } else if (type.equals(Type.LESS_THAN_OR_EQUAL)) {
      s.append(" <= ");
    } else if (type.equals(Type.GREATER_THAN)) {
      s.append(" > ");
    } else if (type.equals(Type.GREATER_THAN_OR_EQUAL)) {
      s.append(" >= ");
    } else if (type.equals(Type.ADD)) {
      s.append(" + ");
    } else if (type.equals(Type.SUBTRACT)) {
      s.append(" - ");
    } else if (type.equals(Type.MULTIPLY)) {
      s.append(" * ");
    } else if (type.equals(Type.DIVIDE)) {
      s.append(" / ");
    } else /* if (type.equals(Type.MODULUS)) */ {
      s.append(" % ");
    }
    s.append(right.prettyString(depth));
    s.append(")");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTBinaryExpression {\n");
    s.append(indent(depth + 1) + "left: " + left.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "right: " + right.debugString(depth + 1) + ",\n");
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
