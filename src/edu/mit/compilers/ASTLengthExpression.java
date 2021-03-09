package edu.mit.compilers;

import static edu.mit.compilers.Utilities.indent;

class ASTLengthExpression implements ASTExpression {

  private final String identifier;

  public ASTLengthExpression(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("len(");
    s.append(identifier);
    s.append(")");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTLengthExpression {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
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
