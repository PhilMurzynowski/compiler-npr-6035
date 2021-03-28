package edu.mit.compilers.hl;

public class HLLoadArrayExpression implements HLLoadExpression {

  private final HLArrayFieldDeclaration declaration;
  private final HLExpression index;

  public HLLoadArrayExpression(HLArrayFieldDeclaration declaration, HLExpression index) {
    this.declaration = declaration;
    this.index = index;
  }

  public HLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getIndex() {
    return index;
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
