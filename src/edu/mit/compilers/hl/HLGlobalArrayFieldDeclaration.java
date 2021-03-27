package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

class HLGlobalArrayFieldDeclaration implements HLArrayFieldDeclaration {

  private final VariableType type;
  private final String identifier;
  private final int length;

  public HLGlobalArrayFieldDeclaration(
    final VariableType type,
    final String identifier,
    final int length)
  {
    throw new RuntimeException("not implemented");
  }

}
