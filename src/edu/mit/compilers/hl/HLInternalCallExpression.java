package edu.mit.compilers.hl;

import java.util.List;

public class HLInternalCallExpression implements HLCallExpression {

  private final HLMethodDeclaration declaration;
  private final List<HLArgument> arguments;

  public HLInternalCallExpression(HLMethodDeclaration declaration, List<HLArgument> arguments) {
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
