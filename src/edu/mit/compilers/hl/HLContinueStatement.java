package edu.mit.compilers.hl;

public class HLContinueStatement implements HLStatement {

  public HLContinueStatement() { }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLContinueStatement { }");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
