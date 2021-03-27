package edu.mit.compilers.ll;

import java.util.List;

public class LLProgram implements LLNode {

  private final List<LLImportDeclaration> importDeclarations;
  private final List<LLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLStringLiteralDeclaration> stringLiteralDeclarations;
  private final List<LLMethodDeclaration> methodDeclarations;

  public LLProgram(
    final List<LLImportDeclaration> importDeclarations,
    final List<LLGlobalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<LLGlobalArrayFieldDeclaration> arrayFieldDeclarations,
    final List<LLStringLiteralDeclaration> stringLiteralDeclarations,
    final List<LLMethodDeclaration> methodDeclarations)
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
