package edu.mit.compilers.ll;

public class LLImportDeclaration implements LLDeclaration {

  private final String identifier;

  public LLImportDeclaration(String identifier) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public location() {
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
