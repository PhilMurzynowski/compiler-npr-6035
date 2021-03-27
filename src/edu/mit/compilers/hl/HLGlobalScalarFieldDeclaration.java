package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

class HLGlobalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final String identifier;

  public HLGlobalScalarFieldDeclaration(
    final VariableType type,
    final String identifier)
  {
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
