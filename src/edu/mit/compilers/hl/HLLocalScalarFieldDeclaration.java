package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class HLLocalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;
  private Optional<LLLocalScalarFieldDeclaration> ll;

  public HLLocalScalarFieldDeclaration(
    final VariableType type,
    final int index)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public void setLL(LLLocalScalarFieldDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLLocalScalarFieldDeclaration getLL() {
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
