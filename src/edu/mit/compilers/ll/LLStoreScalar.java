package edu.mit.compilers.ll;

public class LLStoreScalar implements LLNode {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration expression;

  public LLStoreScalar(LLScalarFieldDeclaration declaration, LLDeclaration expression) {
    throw new UnsupportedOperationException("not implemented");
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
