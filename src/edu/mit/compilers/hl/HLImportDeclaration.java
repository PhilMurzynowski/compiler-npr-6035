package edu.mit.compilers.hl;

class HLImportDeclaration implements HLNode {
  
  private final String identifier;

  public HLImportDeclaration() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
