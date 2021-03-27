package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

public class HLImportDeclaration implements HLNode {
  
  private final String identifier;
  private Optional<LLImportDeclaration> ll;

  public HLImportDeclaration(final String identifier) {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLImportDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLImportDeclaration getLL() {
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
