package edu.mit.compilers.hl;

// TODO: Noah (debugString)
public class HLCallStatement implements HLStatement {

  private final HLCallExpression call;

  public HLCallStatement(final HLCallExpression call) {
    this.call = call;
  }

  public HLCallExpression getCall() {
    return call;
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
