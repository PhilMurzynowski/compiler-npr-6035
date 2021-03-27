package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;

public class LLBasicBlock implements LLNode {

  private final int index;
  private final List<LLInstruction> instructions;
  private final Optional<LLBasicBlock> trueTarget;
  private final Optional<LLBasicBlock> falseTarget;

  public LLBasicBlock(int index, List<LLInstruction> instructions, Optional<LLBasicBlock> trueTarget, Optional<LLBasicBlock> falseTarget) {
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
