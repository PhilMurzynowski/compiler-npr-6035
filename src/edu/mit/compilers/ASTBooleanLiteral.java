package edu.mit.compilers;

import static edu.mit.compilers.Utilities.indent;

class ASTBooleanLiteral implements ASTExpression {

  private final boolean value;
  
  public ASTBooleanLiteral(boolean value) {
    this.value = value;
  }

  @Override
  public String prettyString(int depth) {
    return value + "";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTBooleanLiteral {\n");
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
