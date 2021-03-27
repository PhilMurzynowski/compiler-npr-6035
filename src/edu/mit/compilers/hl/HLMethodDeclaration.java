package edu.mit.compilers.hl;

class HLMethodDeclaration implements HLNode {

  private final String identifier;
  private final HLBlock body;

  public HLMethodDeclaration() {
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
