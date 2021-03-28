package edu.mit.compilers.hl;

public class HLLoadScalarExpression implements HLLoadExpression {

  private final HLScalarFieldDeclaration declaration;

  public HLLoadScalarExpression(HLScalarFieldDeclaration declaration) {
    this.declaration = declaration;
  }

  public HLScalarFieldDeclaration getDeclaration() {
    return declaration;
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
