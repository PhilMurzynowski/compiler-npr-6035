package edu.mit.compilers.hl;

class HLForStatement implements HLStatement {
  private final HLStoreScalarStatement initial;
  private final HLExpression condition;
  private final HLStoreStatement update;
  private final HLBlock body;
}
