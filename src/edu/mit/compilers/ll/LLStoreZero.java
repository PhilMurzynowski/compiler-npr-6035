package edu.mit.compilers.ll;

public class LLStoreZero implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;

  public LLStoreZero(LLScalarFieldDeclaration declaration) {
    this.declaration = declaration;
  }

  @Override
  public String debugString(int depth) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
