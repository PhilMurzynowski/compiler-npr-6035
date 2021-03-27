package edu.mit.compilers.ast;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTBinaryExpression implements ASTExpression {

  private final TextLocation textLocation;
  private final ASTExpression left;
  private final BinaryExpressionType type;
  private final ASTExpression right;

  private ASTBinaryExpression(TextLocation textLocation, ASTExpression left, BinaryExpressionType type, ASTExpression right) {
    this.textLocation = textLocation;
    this.left = left;
    this.type = type;
    this.right = right;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private ASTExpression expression;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      expression = null;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public Builder withExpression(BinaryExpressionType type, ASTExpression expression) {
      assert this.expression != null;

      this.expression = new ASTBinaryExpression(textLocation, this.expression, type, expression);
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

  public BinaryExpressionType getType() {
	  return type;
  }

  private boolean acceptsInteger() {
    return (type.equals(BinaryExpressionType.EQUAL))
      || (type.equals(BinaryExpressionType.NOT_EQUAL))
      || (type.equals(BinaryExpressionType.LESS_THAN))
      || (type.equals(BinaryExpressionType.LESS_THAN_OR_EQUAL))
      || (type.equals(BinaryExpressionType.GREATER_THAN))
      || (type.equals(BinaryExpressionType.GREATER_THAN_OR_EQUAL))
      || (type.equals(BinaryExpressionType.ADD))
      || (type.equals(BinaryExpressionType.SUBTRACT))
      || (type.equals(BinaryExpressionType.MULTIPLY))
      || (type.equals(BinaryExpressionType.DIVIDE))
      || (type.equals(BinaryExpressionType.MODULUS));
  }

  private boolean acceptsBoolean() {
    return (type.equals(BinaryExpressionType.OR))
      || (type.equals(BinaryExpressionType.AND))
      || (type.equals(BinaryExpressionType.EQUAL))
      || (type.equals(BinaryExpressionType.NOT_EQUAL));
  }

  public boolean acceptsType(VariableType type) {
    if (type.equals(VariableType.INTEGER)) {
      return acceptsInteger();
    } else /* if (type.equals(VariableType.BOOLEAN)) */ {
      return acceptsBoolean();
    }
  }

  private boolean returnsInteger() {
    return (type.equals(BinaryExpressionType.ADD))
      || (type.equals(BinaryExpressionType.SUBTRACT))
      || (type.equals(BinaryExpressionType.MULTIPLY))
      || (type.equals(BinaryExpressionType.DIVIDE))
      || (type.equals(BinaryExpressionType.MODULUS));
  }

  @SuppressWarnings("unused")
  private boolean returnsBoolean() {
    return (type.equals(BinaryExpressionType.OR))
      || (type.equals(BinaryExpressionType.AND))
      || (type.equals(BinaryExpressionType.EQUAL))
      || (type.equals(BinaryExpressionType.NOT_EQUAL))
      || (type.equals(BinaryExpressionType.LESS_THAN))
      || (type.equals(BinaryExpressionType.LESS_THAN_OR_EQUAL))
      || (type.equals(BinaryExpressionType.GREATER_THAN))
      || (type.equals(BinaryExpressionType.GREATER_THAN_OR_EQUAL));
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
    s.append(left.prettyString(depth));
    if (type.equals(BinaryExpressionType.OR)) {
      s.append(" || ");
    } else if (type.equals(BinaryExpressionType.AND)) {
      s.append(" && ");
    } else if (type.equals(BinaryExpressionType.EQUAL)) {
      s.append(" == ");
    } else if (type.equals(BinaryExpressionType.NOT_EQUAL)) {
      s.append(" != ");
    } else if (type.equals(BinaryExpressionType.LESS_THAN)) {
      s.append(" < ");
    } else if (type.equals(BinaryExpressionType.LESS_THAN_OR_EQUAL)) {
      s.append(" <= ");
    } else if (type.equals(BinaryExpressionType.GREATER_THAN)) {
      s.append(" > ");
    } else if (type.equals(BinaryExpressionType.GREATER_THAN_OR_EQUAL)) {
      s.append(" >= ");
    } else if (type.equals(BinaryExpressionType.ADD)) {
      s.append(" + ");
    } else if (type.equals(BinaryExpressionType.SUBTRACT)) {
      s.append(" - ");
    } else if (type.equals(BinaryExpressionType.MULTIPLY)) {
      s.append(" * ");
    } else if (type.equals(BinaryExpressionType.DIVIDE)) {
      s.append(" / ");
    } else /* if (type.equals(BinaryExpressionType.MODULUS)) */ {
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
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
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
