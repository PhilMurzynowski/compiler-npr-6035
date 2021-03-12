package edu.mit.compilers;

import static edu.mit.compilers.Utilities.indent;

class ASTStringLiteral implements ASTArgument {

  private final String value;

  public ASTStringLiteral(String value) {
    this.value = value;
  }

  @Override
  public void accept(ASTNode.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void accept(ASTArgument.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    return '"' + value + '"'; // FIXME(rbd): Handle un/escape characters.
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTStringLiteral {\n");
    s.append(indent(depth + 1) + "value: \"" + value + "\",\n"); // FIXME(rbd): Handle un/escape characters.
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
