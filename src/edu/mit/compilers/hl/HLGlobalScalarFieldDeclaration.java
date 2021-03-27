package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

class HLGlobalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final String identifier;

  public HLGlobalScalarFieldDeclaration(
    private final VariableType type,
    private final String identifier)
  {
    throw new RuntimeException("not implemented");
  }

}
