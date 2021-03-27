package edu.mit.compilers.hl;

class HLLocalArrayFieldDeclaration implements HLArrayFieldDeclaration{
  // NOTE: may be changing with hoisting
  private final int index;
  private final long length;
}
