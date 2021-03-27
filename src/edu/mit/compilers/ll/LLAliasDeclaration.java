package edu.mit.compilers.ll;

public class LLAliasDeclaration implements LLDeclaration {

  private final int index;

  public LLAliasDeclaration(int index) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String location() {
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
