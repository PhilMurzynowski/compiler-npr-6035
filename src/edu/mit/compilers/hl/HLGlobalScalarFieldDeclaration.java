package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class HLGlobalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final String identifier;
  private Optional<LLGlobalScalarFieldDeclaration> ll;

  public HLGlobalScalarFieldDeclaration(
    final VariableType type,
    final String identifier)
  {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLGlobalScalarFieldDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public LLGlobalScalarFieldDeclaration getLL() {
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
