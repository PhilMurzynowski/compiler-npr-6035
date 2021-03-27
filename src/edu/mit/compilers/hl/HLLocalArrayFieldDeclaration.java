package edu.mit.compilers.hl;

class HLLocalArrayFieldDeclaration implements HLArrayFieldDeclaration{

  // NOTE: may be changing with hoisting
  private final int index;
  private final int length;

  public HLLocalArrayFieldDeclaration(
    final int index,
    final int length)
  {
    throw new RuntimeException("not implemented");
  }

}
