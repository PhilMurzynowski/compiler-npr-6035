package edu.mit.compilers.ll;

public class LLGlobalScalarFieldDeclaration implements LLScalarFieldDeclaration {

  private final String identifier;

  public LLGlobalScalarFieldDeclaration (String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String location() {
    return identifier;
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
