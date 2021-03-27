package edu.mit.compilers.hl;

public class HLIntegerLiteral implements HLExpression {

  private final long value;

  public HLIntegerLiteral() {
    throw new UnsupportedOperationException("not implemented");
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
