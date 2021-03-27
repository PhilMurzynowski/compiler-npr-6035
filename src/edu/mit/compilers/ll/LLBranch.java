package edu.mit.compilers.ll;

public class LLBranch implements LLInstruction {

  private final LLDeclaration condition;
  private final LLLabelDeclaration trueTarget;
  private final LLLabelDeclaration falseTarget;

  public LLBranch(LLDeclaration condition, LLLabelDeclaration trueTarget, LLLabelDeclaration falseTarget) {
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
