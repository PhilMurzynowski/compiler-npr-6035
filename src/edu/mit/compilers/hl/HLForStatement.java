package edu.mit.compilers.hl;

class HLForStatement implements HLStatement {

  private final HLStoreScalarStatement initial;
  private final HLExpression condition;
  private final HLStoreStatement update;
  private final HLBlock body;

  public HLForStatement(
      HLStoreScalarStatement initial,
      HLExpression condition,
      HLStoreStatement update,
      HLBlock body)
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
