package edu.mit.compilers.ll;

public class LLStoreArray implements LLInstruction {
  
  private final LLArrayFieldDeclaration declaration;
  private final LLDeclaration index;
  private final LLDeclaration expression;

  public LLStoreArray(LLArrayFieldDeclaration declaration, LLDeclaration index, LLDeclaration expression) {
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
