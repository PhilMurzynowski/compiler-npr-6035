package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

public class HLLocalArrayFieldDeclaration implements HLArrayFieldDeclaration {

  // NOTE: may be changing with hoisting
  private final int index;
  private final int length;
  private Optional<LLLocalArrayFieldDeclaration> ll;

  public HLLocalArrayFieldDeclaration(
    final int index,
    final int length)
  {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLLocalArrayFieldDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLLocalArrayFieldDeclaration getLL() {
    throw new RuntimeException("not implemented");
  }

  public int getLength() {
    return this.length;
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
