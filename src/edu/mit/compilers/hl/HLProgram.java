package edu.mit.compilers.hl;

import java.util.List;

public class HLProgram implements HLNode {

  private final List<HLImportDeclaration> importDeclarations;
  private final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStringLiteralDeclaration> stringLiteralDeclarations;
  private final List<HLMethodDeclaration> methodDeclarations;

  public HLProgram(
    final List<HLImportDeclaration> importDeclarations,
    final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations,
    final List<HLStringLiteralDeclaration> stringLiteralDeclarations,
    final List<HLMethodDeclaration> methodDeclarations)
  {
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
