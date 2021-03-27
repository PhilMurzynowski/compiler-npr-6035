package edu.mit.compilers.hl;

public class HLContinueStatement implements HLStatement {

  public HLContinueStatement() {
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
