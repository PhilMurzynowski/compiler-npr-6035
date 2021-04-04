package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static edu.mit.compilers.common.Utilities.indent;

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

  public LLBasicBlock simplify(Map<LLBasicBlock, Set<LLBasicBlock>> backEdges, Map<LLBasicBlock, LLBasicBlock> simplified) {
    assert !generated : "cannot simplify because basic block has already been generated";

    if (falseTarget.isPresent()) {
      assert trueTarget.isPresent() : "false target exists without matching true target";

      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), getTrueTarget().simplify(backEdges, simplified));
      }

      if (!simplified.containsKey(getFalseTarget())) {
        simplified.put(getFalseTarget(), getFalseTarget().simplify(backEdges, simplified));
      }

      LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());
      LLBasicBlock simplifiedFalseTarget = simplified.get(getFalseTarget());

      return new LLBasicBlock(instructions, Optional.of(simplifiedTrueTarget), Optional.of(simplifiedFalseTarget));
    } else if (trueTarget.isPresent()) {
      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), getTrueTarget().simplify(backEdges, simplified));
      }

      LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());

      if (backEdges.get(trueTarget.get()).size() == 1) {
        List<LLInstruction> resultInstructions = new ArrayList<>(instructions);
        resultInstructions.addAll(simplifiedTrueTarget.instructions);

        return new LLBasicBlock(resultInstructions, simplifiedTrueTarget.trueTarget, simplifiedTrueTarget.falseTarget);
      } else {
        return new LLBasicBlock(instructions, Optional.of(simplifiedTrueTarget), Optional.empty());
      }
    } else {
      return this;
    }
  }

  public LLBasicBlock getExit() {
    if (falseTarget.isPresent()) {
      LLBasicBlock trueExit = trueTarget.get().getExit();
      LLBasicBlock falseExit = falseTarget.get().getExit();

      assert trueExit == falseExit : "true and false target exits do not *reference* the same basic block";

      return trueExit;
    } else if (trueTarget.isPresent()) {
      return trueTarget.get().getExit();
    } else {
      return this;
    }
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String location() {
    return "BB" + index;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLBasicBlock {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "instructions: [\n");
    for (LLInstruction instruction : instructions) {
      s.append(indent(depth + 2) + instruction.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    if (trueTarget.isPresent()) {
      s.append(indent(depth + 1) + "trueTarget: " + trueTarget.get().index + ",\n");
    }
    if (falseTarget.isPresent()) {
      s.append(indent(depth + 1) + "falseTarget: " + falseTarget.get().index + ",\n");
    }
    s.append(indent(depth + 1) + "generated: " + generated + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  @Override
  public int hashCode() {
    return index;
  }

}
