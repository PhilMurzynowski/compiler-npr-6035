package edu.mit.compilers.hl;

import java.util.List;

class HLExternalCallExpression implements HLCallExpression {

  private final HLImportDeclaration declaration;
  private final List<HLArgument> arguments;

  public HLExternalCallExpression(HLImportDeclaration declaration, List<HLArgument> arguments) {
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
