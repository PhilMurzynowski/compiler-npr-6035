package edu.mit.compilers.ll;

import java.util.List;

public class LLInternalCall implements LLNode {

  private final LLMethodDeclaration declaration;
  private final List<LLDeclaration> arguments;
  private final LLDeclaration result;

  public LLInternalCall(LLMethodDeclaration declaration, List<LLDeclaration> arguments, LLDeclaration result) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
