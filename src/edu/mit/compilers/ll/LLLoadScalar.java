package edu.mit.compilers.ll;

public class LLLoadScalar implements LLNode {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration result;
  
  public LLLoadScalar(LLScalarFieldDeclaration declaration, LLDeclaration result) {
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
