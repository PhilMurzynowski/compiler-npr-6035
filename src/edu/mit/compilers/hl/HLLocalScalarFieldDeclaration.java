package edu.mit.compilers.hl;

import edu.mit.compilers.common.VariableType;

public class HLLocalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;

  public HLLocalScalarFieldDeclaration(
    final VariableType type,
    final int index)
  {
    throw new UnsupportedOperationException("not implemented");
  }

}
