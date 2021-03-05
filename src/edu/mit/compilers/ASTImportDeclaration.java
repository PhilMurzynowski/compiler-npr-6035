package edu.mit.compilers;

class ASTImportDeclaration implements ASTNode {

  private final String identifier;

  public ASTImportDeclaration(String identifier) {
    this.identifier = identifier;
  }

  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    throw new RuntimeException("not implemented");
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
