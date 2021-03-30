package edu.mit.compilers.ll;

public class LLStoreScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration expression;

  public LLStoreScalar(LLScalarFieldDeclaration declaration, LLDeclaration expression) {
    this.declaration = declaration;
    this.expression = expression;
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
