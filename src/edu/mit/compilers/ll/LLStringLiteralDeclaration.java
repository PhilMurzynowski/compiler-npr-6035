package edu.mit.compilers.ll;

public class LLStringLiteralDeclaration implements LLDeclaration {

  private final int index;
  private final String value;

  public LLStringLiteralDeclaration(int index, String value) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String location() {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
