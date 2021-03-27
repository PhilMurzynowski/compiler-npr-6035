package edu.mit.compilers.hl;

class HLLoadArrayExpression implements HLExpression {

  private final HLArrayFieldDeclaration declaration;
  private final HLExpression index;

  public HLLoadArrayExpression(HLArrayFieldDeclaration declaration, HLExpression index) {
    throw new UnsupportedOperationException("not implemented");
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
