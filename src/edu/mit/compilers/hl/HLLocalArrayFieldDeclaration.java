package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

// TODO: Noah (debugString)
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
    // if (this.ll.isPresent()) {
    //   throw new RuntimeException("ll has already been set");
    // } else {
      this.ll = Optional.of(ll);
    // }
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
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
