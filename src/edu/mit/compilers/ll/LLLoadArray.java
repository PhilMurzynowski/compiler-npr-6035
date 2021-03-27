package edu.mit.compilers.ll;

public class LLLoadArray implements LLNode {

  private final LLArrayFieldDeclaration location;
  private final LLDeclaration index;
  private final LLDeclaration result;

  public LLLoadArray(LLArrayFieldDeclaration location, LLDeclaration index, LLDeclaration result) {
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
