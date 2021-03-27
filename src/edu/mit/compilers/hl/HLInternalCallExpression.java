package edu.mit.compilers.hl;

import java.util.List;

public class HLInternalCallExpression implements HLCallExpression {

  private final HLMethodDeclaration declaration;
  private final List<HLArgument> arguments;

  public HLInternalCallExpression() {
    throw new UnsupportedOperationException("not implemented");
  }

}
