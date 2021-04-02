package edu.mit.compilers.ll;

// DONE: Noah
public class LLStringLiteralDeclaration implements LLDeclaration {

  private final int index;
  private final String value;

  public LLStringLiteralDeclaration(int index, String value) {
    this.index = index;
    this.value = value;
  }

  public int getIndex() {
    return index;
  }

  public String getValue() {
    return value;
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
