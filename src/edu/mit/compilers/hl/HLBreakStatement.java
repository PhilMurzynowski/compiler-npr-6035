package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLBreakStatement implements HLStatement {

  public HLBreakStatement() {
    throw new RuntimeException("not implemented");
  }
  
  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLIfStatement { }\n");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
