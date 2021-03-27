package edu.mit.compilers.ll;

public class LLLoadScalar implements LLNode {

  private final LLScalarDeclaration declaration;
  private final LLDeclaration result;
  
  public LLLoadScalar(
    final LLScalarDeclaration declaration,
    final LLDeclaration result)
  {
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
