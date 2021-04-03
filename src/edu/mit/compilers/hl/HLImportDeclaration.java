package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

public class HLImportDeclaration implements HLNode {
  
  private final String identifier;
  private Optional<LLImportDeclaration> ll;

  public HLImportDeclaration(final String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifer() {
    return this.identifier;
  }

  public void setLL(LLImportDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  public LLImportDeclaration getLL() {
    if (this.ll.isPresent()) {
      return this.ll.get();
    }
    throw new RuntimeException("ll is empty");
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
