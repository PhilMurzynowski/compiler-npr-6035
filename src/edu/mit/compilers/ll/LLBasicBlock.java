package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

public class LLBasicBlock implements LLNode {

  private static int count = 0;

  private final int index;
  private final List<LLInstruction> instructions;
  private Optional<LLBasicBlock> trueTarget;
  private Optional<LLBasicBlock> falseTarget;

  // NOTE(rbd): Please try not to add anything to this class. Let me know if you do. :)

  public LLBasicBlock(LLInstruction ...instructions) {
    index = count++;
    this.instructions = Arrays.asList(instructions);
    trueTarget = Optional.empty();
    falseTarget = Optional.empty();
  }

  public void setTrueTarget(LLBasicBlock trueTarget) {
    if (this.trueTarget.isPresent()) {
      throw new RuntimeException("true target has already been set");
    } else {
      this.trueTarget = Optional.of(trueTarget);
    }
  }

  public void setFalseTarget(LLBasicBlock falseTarget) {
    if (this.falseTarget.isPresent()) {
      throw new RuntimeException("false target has already been set");
    } else {
      this.falseTarget = Optional.of(falseTarget);
    }
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
