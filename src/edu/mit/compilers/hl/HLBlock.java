package edu.mit.compilers.hl;

import java.util.List;

class HLBlock implements HLNode {

  private final List<HLArgumentDeclaration> argumentDeclarations;
  private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLArrayArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStatement> statements;

  // NOTE: may want a builder
  public HLBlock( 
    private final List<HLArgumentDeclaration> argumentDeclarations,
    private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations,
    private final List<HLArrayArrayFieldDeclaration> arrayFieldDeclarations,
    private final List<HLStatement> statements)
  {
    throw new RuntimeException("not implemented");
  }

}
