package edu.mit.compilers.ll;

import edu.mit.compilers.common.*;

public class LLBinary implements LLNode {

  private final LLDeclaration left;
  private final BinaryExpressionType type;
  private final LLDeclaration right;
  private final LLDeclaration result;

  public LLBinary(LLDeclaration left, BinaryExpressionType type, LLDeclaration right, LLDeclaration result) {
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
