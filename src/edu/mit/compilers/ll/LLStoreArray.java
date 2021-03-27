package edu.mit.compilers.ll;

public class LLStoreArray implements LLNode {
  
  private final LLArrayDeclaration declaration;
  private final LLDeclaration index;
  private final LLDeclaration expression;

  public LLStoreArray(
    final LLArrayDeclaration declaration,
    final LLDeclaration index,
    final LLDeclaration expression)
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
