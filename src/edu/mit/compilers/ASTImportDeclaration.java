package edu.mit.compilers;

import static edu.mit.compilers.Utilities.indent;

class ASTImportDeclaration implements ASTNode {

  private final String identifier;

  public ASTImportDeclaration(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String prettyString(int depth) {
    return "import " + identifier + ";";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTImportDeclaration {\n");
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
