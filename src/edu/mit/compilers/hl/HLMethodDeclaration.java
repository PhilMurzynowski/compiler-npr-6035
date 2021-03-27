package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

public class HLMethodDeclaration implements HLNode {

  private final String identifier;
  private final HLBlock body;
  private Optional<LLMethodDeclaration> ll;

  public HLMethodDeclaration(
    final String identifier,
    final HLBlock body)
  {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLMethodDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLMethodDeclaration getLL() {
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
