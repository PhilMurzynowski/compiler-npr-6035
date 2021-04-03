package edu.mit.compilers.ll;

public class LLImportDeclaration implements LLDeclaration {

  private final String identifier;

  public LLImportDeclaration(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
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
