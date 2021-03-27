package edu.mit.compilers.ll;

public class LLJump implements LLInstruction {

  private final LLLabelDeclaration target;

  public LLJump(LLLabelDeclaration target) {
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
