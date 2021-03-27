package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

public class HLStringLiteralDeclaration implements HLNode {

  private final int index;
  private final String value;
  private Optional<LLStringLiteralDeclaration> ll;

  public HLStringLiteralDeclaration(
    final int index,
    final String value)
  {
    throw new UnsupportedOperationException("no implemented");
  }

  public void setLL(LLStringLiteralDeclaration ll) {
    throw new RuntimeException("not implemented");
  }

  public LLStringLiteralDeclaration getLL() {
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
