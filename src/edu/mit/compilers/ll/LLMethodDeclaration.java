package edu.mit.compilers.ll;

public class LLMethodDeclaration implements LLDeclaration {

  private final List<LLArgumentDeclartion> argumentDeclartions;
  private final List<LLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLAliasDeclaration> aliasDeclarations;
  private final LLControlFlowGraph body;
  
  @Override
  public location() {
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
