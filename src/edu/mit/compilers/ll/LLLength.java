package edu.mit.compilers.ll;

public class LLLength implements LLNode {

  private final LLArrayDeclaration declaration;
  private final LLDeclaration result;

  public LLLength(LLArrayDeclaration declaration, LLDeclaration result) {
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
