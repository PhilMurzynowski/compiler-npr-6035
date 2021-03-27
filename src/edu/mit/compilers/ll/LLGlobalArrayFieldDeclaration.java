package edu.mit.compilers.ll;

public class LLGlobalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final String identifier;
  private final int length;

  public LLGlobalArrayFieldDeclaration(String identifier, int length) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String location() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String index(String register) {
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
