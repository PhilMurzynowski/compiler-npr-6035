package edu.mit.compilers.hl;

class HLMethodDeclaration implements HLNode {

  private final String identifier;
  private final HLBlock body;

  public HLMethodDeclaration(
    private final String identifier,
    private final HLBlock body)
  {
    throw new RuntimeException("not implemented");
  }

}
