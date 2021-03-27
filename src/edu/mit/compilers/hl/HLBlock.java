package edu.mit.compilers.hl;

import java.util.List;

class HLBlock implements HLNode {

  private final List<HLArgumentDeclaration> argumentDeclarations;
  private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStatement> statements;

  public HLBlock() {
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
