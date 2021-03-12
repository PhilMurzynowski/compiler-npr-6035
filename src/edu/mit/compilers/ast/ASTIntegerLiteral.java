package edu.mit.compilers.ast;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTIntegerLiteral implements ASTExpression {

  private final long value;

  public ASTIntegerLiteral(long value) {
    this.value = value;
  }

  @Override
  public void accept(ASTArgument.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void accept(ASTExpression.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void accept(ASTNode.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    return value + "";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTIntegerLiteral {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
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
