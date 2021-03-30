package edu.mit.compilers.ll;

public class LLStoreArrayZero implements LLInstruction {
  
  private final LLArrayFieldDeclaration declaration;
  private final int length;

  public LLStoreArrayZero(LLArrayFieldDeclaration declaration, int length) {
    this.declaration = declaration;
    this.length = length;
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
