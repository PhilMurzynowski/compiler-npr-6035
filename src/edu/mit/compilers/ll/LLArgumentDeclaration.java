package edu.mit.compilers.ll;

public class LLArgumentDeclaration implements LLScalarFieldDeclaration {

  private final int index;

  public LLArgumentDeclaration(int index) {
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
