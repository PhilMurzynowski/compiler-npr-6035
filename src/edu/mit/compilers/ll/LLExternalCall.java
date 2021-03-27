package edu.mit.compilers.ll;

import java.util.List;

public class LLExternalCall implements LLNode {
  
  private final LLImportDeclaration declaration;
  private final List<LLDeclaration> arguments;
  private final LLDeclaration result;

  public LLExternalCall(
    final LLImportDeclaration declaration,
    final List<LLDeclaration> arguments,
    final LLDeclaration result)
  {
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
