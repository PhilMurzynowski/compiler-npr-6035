package edu.mit.compilers.hl;

public class HLStringLiteralDeclaration {

  private final int index;
  private final String value;

  public HLStringLiteralDeclaration() {
    throw new UnsupportedOperationException("no implemented");
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
