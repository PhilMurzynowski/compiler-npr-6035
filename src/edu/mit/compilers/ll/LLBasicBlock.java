package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;

public class LLBasicBlock implements LLDeclaration {

  private static int count = 0;

  private final int index;
  private final List<LLInstruction> instructions;
  private Optional<LLBasicBlock> trueTarget;
  private Optional<LLBasicBlock> falseTarget;
  private boolean generated;

  // NOTE(rbd): Please try not to add anything to this class. Let me know if you do. :)

  public LLBasicBlock(List<LLInstruction> instructions, Optional<LLBasicBlock> trueTarget, Optional<LLBasicBlock> falseTarget) {
    index = count++;
    this.instructions = instructions;
    this.trueTarget = trueTarget;
    this.falseTarget = falseTarget;
    generated = false;
  }

  public LLBasicBlock(List<LLInstruction> instructions) {
    this(instructions, Optional.empty(), Optional.empty());
  }

  public LLBasicBlock(LLInstruction ...instructions) {
    this(Arrays.asList(instructions));
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

  public void setGenerated() {
    if (generated) {
      throw new RuntimeException("basicBlock has already been generated");
    } else {
      generated = true;
    }
  }

  public boolean hasTrueTarget() {
    return trueTarget.isPresent();
  }

  public boolean hasFalseTarget() {
    return falseTarget.isPresent();
  }

  public List<LLInstruction> getInstructions() {
    return instructions;
  }

  public LLBasicBlock getTrueTarget() {
    if (trueTarget.isEmpty()) {
      throw new RuntimeException("trueTarget does not exist");
    } else {
      return trueTarget.get();
    }
  }

  public LLBasicBlock getFalseTarget() {
    if (falseTarget.isEmpty()) {
      throw new RuntimeException("falseTarget does not exist");
    } else {
      return falseTarget.get();
    }
  }

  public boolean isGenerated() {
    return generated;
  }

  public LLBasicBlock simplify() {
    assert !generated : "cannot simplify because basic block has already been generated";

    if (falseTarget.isPresent()) {
      assert trueTarget.isPresent() : "false target exists without matching true target";

      LLBasicBlock simplifiedTrueTarget = getTrueTarget().simplify();
      LLBasicBlock simplifiedFalseTarget = getFalseTarget().simplify();

      return new LLBasicBlock(instructions, Optional.of(simplifiedTrueTarget), Optional.of(simplifiedFalseTarget));
    } else if (trueTarget.isPresent()) {
      LLBasicBlock simplifiedTrueTarget = getTrueTarget().simplify();

      List<LLInstruction> resultInstructions = new ArrayList<>(instructions);
      resultInstructions.addAll(simplifiedTrueTarget.instructions);

      return new LLBasicBlock(resultInstructions, simplifiedTrueTarget.trueTarget, simplifiedTrueTarget.falseTarget);
    } else {
      return this;
    }
  }

  @Override
  public String location() {
    return "BB" + index;
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
