package edu.mit.compilers.hl;

import java.util.List;

class HLBlock implements HLNode {

  private final List<HLArgumentDeclaration> argumentDeclarations;
  private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLArrayArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStatement> statements;

  public HLBlock() {
    throw new RuntimeException("not implemented");
  }

}
