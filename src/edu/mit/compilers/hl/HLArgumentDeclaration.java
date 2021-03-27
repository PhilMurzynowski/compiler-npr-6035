package edu.mit.compilers.hl;

import edu.mit.compilers.common.*;

class HLArgumentDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;

  public HLArgumentDeclaration() {
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
