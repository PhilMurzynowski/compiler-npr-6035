package edu.mit.compilers.hl;

import java.util.List;

class HLProgram implements HLNode {

  private final List<HLImportDeclaration> importDeclarations;
  private final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStringLiteralDeclaration> stringLiteralDeclarations;
  private final List<HLMethodDeclaration> methodDeclarations;

  public HLProgram() {
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
