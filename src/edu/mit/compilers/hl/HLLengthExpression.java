package edu.mit.compilers.hl;

public class HLLengthExpression implements HLExpression {

  private final HLArrayFieldDeclaration declaration;

  public HLLengthExpression(HLArrayFieldDeclaration declaration) {
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
