package edu.mit.compilers.hl;

public class HLStoreScalarStatement implements HLStoreStatement {

  private final HLScalarFieldDeclaration declaration;
  private final HLExpression expression;

  public HLStoreScalarStatement() {
    throw new UnsupportedOperationException("not implemented");
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
