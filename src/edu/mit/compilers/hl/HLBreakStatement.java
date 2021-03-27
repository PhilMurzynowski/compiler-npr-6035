package edu.mit.compilers.hl;

class HLBreakStatement implements HLStatement {

  public HLBreakStatement() {
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
