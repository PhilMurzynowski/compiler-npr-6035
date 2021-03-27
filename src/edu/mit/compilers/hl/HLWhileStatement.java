package edu.mit.compilers.hl;

public class HLWhileStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;

  public HLWhileStatement(HLExpression condition, HLBlock body) {
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
