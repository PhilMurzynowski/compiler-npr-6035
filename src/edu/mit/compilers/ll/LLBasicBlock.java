package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static edu.mit.compilers.common.Utilities.indent;

public class LLBasicBlock implements LLDeclaration {

  private static int count = 0;

  private int index;
  private List<LLInstruction> instructions;
  private Optional<LLBasicBlock> trueTarget;
  private Optional<LLBasicBlock> falseTarget;
  private Set<LLBasicBlock> predecessors;
  private boolean generated;

  public LLBasicBlock(List<LLInstruction> instructions) {
    index = count++;
    this.instructions = instructions;
    this.trueTarget = Optional.empty();
    this.falseTarget = Optional.empty();
    this.predecessors = new HashSet<>();
    generated = false;
  }

  public LLBasicBlock(LLInstruction ...instructions) {
    this(new ArrayList<>(Arrays.asList(instructions)));
  }

  private void setTrueTarget(LLBasicBlock trueTarget) {
    if (this.trueTarget.isPresent()) {
      throw new RuntimeException("true target for BB" + index + " cannot be set to BB" + trueTarget.getIndex() + " as it has already been set to BB" + getTrueTarget().getIndex());
    } else {
      this.trueTarget = Optional.of(trueTarget);
    }
  }

  public static void setTrueTarget(LLBasicBlock src, LLBasicBlock dst) {
    src.setTrueTarget(dst);
    dst.addPredecessor(src);
  }

  private void setFalseTarget(LLBasicBlock falseTarget) {
    if (this.falseTarget.isPresent()) {
      throw new RuntimeException("false target for BB" + index + " cannot be set to BB" + falseTarget.getIndex() + " as it has already been set to BB" + getFalseTarget().getIndex());
    } else {
      this.falseTarget = Optional.of(falseTarget);
    }
  }

  public static void setFalseTarget(LLBasicBlock src, LLBasicBlock dst) {
    src.setFalseTarget(dst);
    dst.addPredecessor(src);
  }

  public void setGenerated() {
    if (generated) {
      throw new RuntimeException("basicBlock has already been generated");
    } else {
      generated = true;
    }
  }

  // NOTE(rbd): This should only be used by LLControlFlowGraph.simplify()!
  public void addPredecessor(LLBasicBlock predecessor) {
    if (this.predecessors.contains(predecessor)) {
      throw new RuntimeException("back edge BB" + predecessor.index + " already exists for BB" + index);
    } else {
      predecessors.add(predecessor);
    }
  }

  public Set<LLBasicBlock> getPredecessors() {
    return predecessors;
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

  public void setInstructions(List<LLInstruction> llInstructions) {
    this.instructions = llInstructions;
  }

  public void addInstructions(List<LLInstruction> llInstructions) {
    this.instructions.addAll(llInstructions);
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

  public LLBasicBlock simplify(Map<LLBasicBlock, LLBasicBlock> simplified) {
    assert !generated : "cannot simplify because basic block has already been generated";

    if (falseTarget.isPresent()) {
      assert trueTarget.isPresent() : "false target exists without matching true target";

      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), new LLBasicBlock());
        simplified.get(getTrueTarget()).subsume(getTrueTarget().simplify(simplified));
      }

      if (!simplified.containsKey(getFalseTarget())) {
        simplified.put(getFalseTarget(), new LLBasicBlock());
        simplified.get(getFalseTarget()).subsume(getFalseTarget().simplify(simplified));
      }

      final LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());
      final LLBasicBlock simplifiedFalseTarget = simplified.get(getFalseTarget());

      final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions);

      // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
      simplifiedBB.setTrueTarget(simplifiedTrueTarget);
      simplifiedBB.setFalseTarget(simplifiedFalseTarget);

      return simplifiedBB;
    } else if (trueTarget.isPresent()) {
      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), new LLBasicBlock());
        simplified.get(getTrueTarget()).subsume(getTrueTarget().simplify(simplified));
      }

      final LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());

      if (getTrueTarget().predecessors.size() == 1) {
        final List<LLInstruction> resultInstructions = new ArrayList<>(instructions);
        resultInstructions.addAll(simplifiedTrueTarget.instructions);

        final LLBasicBlock simplifiedBB = new LLBasicBlock(resultInstructions);

        if (simplifiedTrueTarget.hasTrueTarget()) {
          // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
          simplifiedBB.setTrueTarget(simplifiedTrueTarget.getTrueTarget());
        }

        if (simplifiedTrueTarget.hasFalseTarget()) {
          // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
          simplifiedBB.setFalseTarget(simplifiedTrueTarget.getFalseTarget());
        }

        return simplifiedBB;
      } else {
        final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions);

        // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
        simplifiedBB.setTrueTarget(simplifiedTrueTarget);

        return simplifiedBB;
      }
    } else {
      return this;
    }
  }

  public int getIndex() {
    return index;
  }

  // NOTE(rbd): This is very unfortunate, I know... This is necessary to handle loops in CFG simplification above.
  private void subsume(LLBasicBlock that) {
    this.index = that.index;
    this.instructions = that.instructions;
    this.trueTarget = that.trueTarget;
    this.falseTarget = that.falseTarget;
    this.generated = that.generated;
  }

  @Override
  public String location() {
    return "BB" + index;
  }

  @Override
  public String prettyString(int depth) {
    return "@BB" + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("BB" + index + ":");
    if (predecessors.size() > 0) {
      s.append(indent(12) + "; preds = ");
      boolean isFirst = true;
      for (LLBasicBlock predecessor : predecessors) {
        if (isFirst) {
          s.append("BB" + predecessor.index);
          isFirst = false;
        } else {
          s.append(", BB" + predecessor.index);
        }
      }
    }
    s.append("\n");
    for (LLInstruction instruction : instructions) {
      s.append(indent(depth + 1) + instruction.prettyString(depth + 1) + "\n");
    }
    if (falseTarget.isPresent()) {
      s.append(indent(depth + 1) + "je " + falseTarget.get().prettyString(depth) + "\n");
    }
    if (trueTarget.isPresent()) {
      s.append(indent(depth + 1) + "jmp " + trueTarget.get().prettyString(depth) + "\n");
    }
    return s.toString().strip();
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
    s.append(indent(depth + 1) + "predecessors: {\n");
    for (LLBasicBlock predecessor : predecessors) {
      s.append(indent(depth + 2) + predecessor.index + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "generated: " + generated + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
