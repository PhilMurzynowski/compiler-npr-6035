package edu.mit.compilers.ll;

import java.util.List;

public class LLMethodDeclaration implements LLDeclaration {

  private final List<LLArgumentDeclaration> argumentDeclartions;
  private final List<LLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLAliasDeclaration> aliasDeclarations;
  private final LLControlFlowGraph body;

  public LLMethodDeclaration(
    List<LLArgumentDeclaration> argumentDeclartions,
    List<LLLocalScalarFieldDeclaration> scalarFieldDeclarations,
    List<LLLocalArrayFieldDeclaration> arrayFieldDeclarations,
    List<LLAliasDeclaration> aliasDeclarations,
    LLControlFlowGraph body)
  {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String location() {
    throw new RuntimeException("not implemented");
  }

  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
