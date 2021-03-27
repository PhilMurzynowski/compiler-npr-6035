package edu.mit.compilers.ll;

public class LLGlobalScalarFieldDeclaration implements LLScalarFieldDeclaration {

  private final String identifier;

  public LLGlobalScalarFieldDeclaration (String identifie) {
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
