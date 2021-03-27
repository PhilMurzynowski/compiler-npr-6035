package edu.mit.compilers.hl;

class HLCallStatement implements HLStatement {

  private final HLCallExpression call;

  public HLCallStatement() {
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
