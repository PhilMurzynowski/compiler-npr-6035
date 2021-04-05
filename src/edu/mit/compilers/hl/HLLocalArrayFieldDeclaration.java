package edu.mit.compilers.hl;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

import edu.mit.compilers.ll.*;

public class HLLocalArrayFieldDeclaration implements HLArrayFieldDeclaration {

  // NOTE: may be changing with hoisting
  private final int index;
  private final HLIntegerLiteral length;
  private Optional<LLLocalArrayFieldDeclaration> ll;

  public HLLocalArrayFieldDeclaration(
    final int index,
    final HLIntegerLiteral length)
  {
    this.index = index;
    this.length = length;
    this.ll = Optional.empty();
  }

  public int getIndex() {
    return index;
  }

  public HLIntegerLiteral getLength() {
    return length;
  }

  public void setLL(LLLocalArrayFieldDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  @Override
  public LLLocalArrayFieldDeclaration getLL() {
    if (ll.isPresent()) {
      return ll.get();
    }
    throw new RuntimeException("ll is empty");
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLLocalArrayFieldDeclaration {\n");
    s.append(indent(depth+1) + "index: " + index + ",\n");
    s.append(indent(depth+1) + "length: " + length.debugString(depth + 1) + ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth+1) + "ll: " + ll.get().debugString(depth+1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
