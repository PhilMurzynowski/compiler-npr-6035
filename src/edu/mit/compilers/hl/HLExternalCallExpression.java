package edu.mit.compilers.hl;

import java.util.List;

class HLExternalCallExpression implements HLCallExpression {

  private final HLImportDeclaration declaration;
  private final List<HLArgument> arguments;

  public HLExternalCallExpression(HLImportDeclaration declaration, List<HLArgument> arguments) {
    throw new UnsupportedOperationException("not implemented");
  }

}
