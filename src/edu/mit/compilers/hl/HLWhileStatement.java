package edu.mit.compilers.hl;

class HLWhileStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;

  public HLWhileStatement(HLExpression condition, HLBlock body) {
    throw new RuntimeException("not implemented");
  }

}
