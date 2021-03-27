package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class HLGlobalArrayFieldDeclaration implements HLArrayFieldDeclaration {

  private final VariableType type;
  private final String identifier;
  private final int length;
  private Optional<LLGlobalArrayFieldDeclaration> ll;

  public HLGlobalArrayFieldDeclaration(
    final VariableType type,
    final String identifier,
    final int length)
  {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLGlobalArrayFieldDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLGlobalArrayFieldDeclaration getLL() {
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
