package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;
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

  public void replaceTrueTarget(LLBasicBlock trueTarget) {
    if (this.trueTarget.isPresent()) {
      this.trueTarget = Optional.of(trueTarget);
    } else {
      throw new RuntimeException("true target for BB" + index + " cannot be replaced with BB" + trueTarget.getIndex() + " as it has not yet been set");
    }
  }

  public static void replaceTrueTarget(LLBasicBlock src, LLBasicBlock dst) {
    src.replaceTrueTarget(dst);
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

  public void clearFalseTarget() {
    if (this.falseTarget.isPresent()) {
      this.falseTarget = Optional.empty();
    } else {
      throw new RuntimeException("false target for BB" + index + " cannot be cleared as it has not yet been set");
    }
  }

  public void replaceFalseTarget(LLBasicBlock falseTarget) {
    if (this.falseTarget.isPresent()) {
      this.falseTarget = Optional.of(falseTarget);
    } else {
      throw new RuntimeException("false target for BB" + index + " cannot be replaced with BB" + falseTarget.getIndex() + " as it has not yet been set");
    }
  }

  public static void replaceFalseTarget(LLBasicBlock src, LLBasicBlock dst) {
    src.replaceFalseTarget(dst);
    dst.addPredecessor(src);
  }

  public void setAlwaysTrue() {
    assert trueTarget.isPresent() : "expected true target";
    assert falseTarget.isPresent() : "expected false target";

    getFalseTarget().removePredecessor(this);
    replaceTrueTarget(this, getTrueTarget());
    falseTarget = Optional.empty();
  }

  public void setAlwaysFalse() {
    assert trueTarget.isPresent() : "expected true target";
    assert falseTarget.isPresent() : "expected false target";

    getTrueTarget().removePredecessor(this);
    replaceTrueTarget(this, getFalseTarget());
    falseTarget = Optional.empty();
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
    // NOTE(rbd): This is not always true. After after simplification, it is possible that both true and false targets
    // are the same.
    // if (this.predecessors.contains(predecessor)) {
    //   throw new RuntimeException("back edge BB" + predecessor.index + " already exists for BB" + index);
    // } else {
      predecessors.add(predecessor);
    // }
  }

  public Set<LLBasicBlock> getPredecessors() {
    return predecessors;
  }

  public void removePredecessor(LLBasicBlock block) {
    predecessors.remove(block);
  }

  public void clearPredecessors() {
    predecessors.clear();
  }

  public Set<LLBasicBlock> getSuccessors() {
    final Set<LLBasicBlock> successors = new HashSet<>();

    if (hasTrueTarget()) {
      successors.add(getTrueTarget());
    }

    if (hasFalseTarget()) {
      successors.add(getFalseTarget());
    }

    return successors;
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

  public int getIndex() {
    return index;
  }

  private boolean canMerge() {
    return !hasFalseTarget()
      && hasTrueTarget()
      && (getTrueTarget().getPredecessors().size() == 1);
  }

  public Optional<LLBasicBlock> merge(Optional<LLBasicBlock> exit, final Set<LLBasicBlock> exceptions) {
    while (canMerge()) {
      final LLBasicBlock next = getTrueTarget();

      instructions.addAll(next.getInstructions());

      // NOTE(rbd): Don't care about predecessors, they are fixed in LLControlFlowGraph.simplify().
      trueTarget = next.trueTarget;
      falseTarget = next.falseTarget;

      if (!hasTrueTarget()) {
        if (exit.isPresent() && exit.get() == next) {
          exit = Optional.of(this);
        } else if (exceptions.contains(next)) {
          exceptions.remove(next);
          exceptions.add(this);
        } else {
          throw new RuntimeException("basic block with no true or false target is not exit or in exceptions");
        }
      }
    }

    return exit;
  }

  @Override
  public String location() {
    return "BB" + index;
  }

  @Override
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need basic block declaration as string");
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
      s.append(indent(depth + 1) + "br " + trueTarget.get().prettyString(depth + 1) + ", " + falseTarget.get().prettyString(depth) + "\n");
    } else if (trueTarget.isPresent()) {
      s.append(indent(depth + 1) + "br " + trueTarget.get().prettyString(depth) + "\n");
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

  // NOTE(rbd): LLBasicBlock is mutable. Use default `.equals()` and `.hashCode()`

  // @Override
  // public boolean equals(Object that) {
  //   throw new RuntimeException("not implemented");
  // }

  // @Override
  // public int hashCode() {
  //   throw new RuntimeException("not implemented");
  // }

}
