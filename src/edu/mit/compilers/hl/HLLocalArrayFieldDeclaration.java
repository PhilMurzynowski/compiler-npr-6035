package edu.mit.compilers.hl;

class HLLocalArrayFieldDeclaration implements HLArrayFieldDeclaration{

  // NOTE: may be changing with hoisting
  private final int index;
  private final long length;

  public HLLocalArrayFieldDeclaration() {
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
