package edu.mit.compilers.ll;

public class LLStoreScalar implements LLNode {

  private final LLScalarDeclaration declaration;
  private final LLDeclaration expression;

  public LLStoreScalar(LLScalarDeclaration declaration, LLDeclaration expression) {
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
