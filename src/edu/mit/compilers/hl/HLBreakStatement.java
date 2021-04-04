package edu.mit.compilers.hl;

public class HLBreakStatement implements HLStatement {

  public HLBreakStatement() { }
  
  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLBreakStatement { }");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
