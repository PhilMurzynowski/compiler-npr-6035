package edu.mit.compilers.hl;

import edu.mit.compilers.common.VariableType;

public class HLLocalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final variabletype type;
  private final int index;

  public HLLocalScalarFieldDeclaration(
    private final variabletype type,
    private final int index)
  {
    throw new UnsupportedOperationException("not implemented");
  }

}
