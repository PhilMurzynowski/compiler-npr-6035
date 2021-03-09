package edu.mit.compilers;

import static edu.mit.compilers.Utilities.indent;

class ASTCharacterLiteral implements ASTExpression {

  private final char value;

  public ASTCharacterLiteral(char value) {
    this.value = value;
  }

  @Override
  public String prettyString(int depth) {
    return "'" + value + "'"; // FIXME(rbd): Handle un/escape characters.
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTCharacterLiteral {\n");
    s.append(indent(depth + 1) + "value: '" + value + "',\n"); // FIXME(rbd): Handle un/escape characters.
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
