package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLImportDeclaration implements HLNode {
  
  private final String identifier;
  private Optional<LLImportDeclaration> ll;

  public HLImportDeclaration(final String identifier) {
    this.identifier = identifier;
    this.ll = Optional.empty();
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
    StringBuilder s = new StringBuilder();
    s.append("LLImportDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth + 1) + "ll: " + ll.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
