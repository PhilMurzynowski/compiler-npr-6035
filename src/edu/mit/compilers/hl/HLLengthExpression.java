package edu.mit.compilers.hl;

class HLLengthExpression implements HLExpression {

  private final HLArrayFieldDeclaration declaration;

  public HLLengthExpression() {
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
