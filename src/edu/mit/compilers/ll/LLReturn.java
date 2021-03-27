package edu.mit.compilers.ll;

public class LLReturn implements LLNode {

  private final LLAliasDeclaration expression;

  public LLReturn(LLAliasDeclaration expression) {
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
