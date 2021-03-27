package edu.mit.compilers.hl;

public class HLStringLiteralDeclaration implements HLNode {

  private final int index;
  private final String value;

  public HLStringLiteralDeclaration(
    final int index,
    final String value)
  {
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
