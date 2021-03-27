package edu.mit.compilers.hl;

public class HLStoreArrayStatement implements HLStoreStatement {

  private final HLArrayFieldDeclaration declaration; 
  private final int index;
  private final HLExpression expression;

  public HLStoreArrayStatement(HLArrayFieldDeclaration declaration, int index, HLExpression expression) {
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
