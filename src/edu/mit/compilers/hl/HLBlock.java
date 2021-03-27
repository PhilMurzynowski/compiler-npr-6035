package edu.mit.compilers.hl;

import java.util.List;

class HLBlock implements HLNode {

  private final List<HLArgumentDeclaration> argumentDeclarations;
  private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStatement> statements;

  // NOTE: may want a builder
  public HLBlock( 
    final List<HLArgumentDeclaration> argumentDeclarations,
    final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<HLArrayFieldDeclaration> arrayFieldDeclarations,
    final List<HLStatement> statements)
  {
    throw new RuntimeException("not implemented");
  }

}
