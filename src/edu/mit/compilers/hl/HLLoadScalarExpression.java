package edu.mit.compilers.hl;

public class HLLoadScalarExpression implements HLExpression {

  private final HLScalarFieldDeclaration declaration;

  public HLLoadScalarExpression() {
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
