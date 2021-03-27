package edu.mit.compilers.ll;

public class LLReturn implements LLInstruction {

  private final LLDeclaration expression;

  public LLReturn(LLDeclaration expression) {
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
