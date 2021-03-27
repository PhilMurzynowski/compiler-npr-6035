package edu.mit.compilers.ll;

public class LLLocalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final int index;
  private final int length;

  public LLLocalArrayFieldDeclaration(int index, int length) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public location() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public index() {
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

