package edu.mit.compilers.hl;

// DONE: Noah
public class HLStoreScalarStatement implements HLStoreStatement {

  private final HLScalarFieldDeclaration declaration;
  private final HLExpression expression;

  public HLStoreScalarStatement(HLScalarFieldDeclaration declaration, HLExpression expression) {
    this.declaration = declaration;
    this.expression = expression;
  }

  public HLScalarFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getExpression() {
    return expression;
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
