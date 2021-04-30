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

  /* private static LLBasicBlock getNextNonEmpty(LLBasicBlock block) {
    if (block.getInstructions().size() == 0) {
      if (block.hasFalseTarget()) {
        throw new RuntimeException("an empty block should not have a false target");
      } else if (block.hasTrueTarget()) {
        return getNextNonEmpty(block.getTrueTarget());
      } else {
        return block;
      }
    } else {
      return block;
    }
  } */

  /* private enum ComparisonResult {
    ALWAYS_TRUE,
    ALWAYS_FALSE,
    AMBIGUOUS;

    public static ComparisonResult fromBoolean(boolean x) {
      return x ? ALWAYS_TRUE : ALWAYS_FALSE;
    }
  } */

  /* private static ComparisonResult evaluateComparison(LLCompare comparison) {
    if (comparison.getLeft() instanceof LLConstantDeclaration left
        && comparison.getRight() instanceof LLConstantDeclaration right) {
      if (comparison.getType().equals(ComparisonType.EQUAL)) {
        return ComparisonResult.fromBoolean(left.getValue() == right.getValue());
      } else if (comparison.getType().equals(ComparisonType.NOT_EQUAL)) {
        return ComparisonResult.fromBoolean(left.getValue() != right.getValue());
      } else if (comparison.getType().equals(ComparisonType.LESS_THAN)) {
        return ComparisonResult.fromBoolean(left.getValue() < right.getValue());
      } else if (comparison.getType().equals(ComparisonType.LESS_THAN_OR_EQUAL)) {
        return ComparisonResult.fromBoolean(left.getValue() <= right.getValue());
      } else if (comparison.getType().equals(ComparisonType.GREATER_THAN)) {
        return ComparisonResult.fromBoolean(left.getValue() > right.getValue());
      } else if (comparison.getType().equals(ComparisonType.GREATER_THAN_OR_EQUAL)) {
        return ComparisonResult.fromBoolean(left.getValue() >= right.getValue());
      } else {
        throw new RuntimeException("unreachable");
      }
    } else {
      return ComparisonResult.AMBIGUOUS;
    }
  } */

  /* public LLBasicBlock simplify(Map<LLBasicBlock, LLBasicBlock> simplified, Set<LLBasicBlock> exits, Set<LLBasicBlock> exceptions, boolean unreachableCodeElimination) {
    assert !generated : "cannot simplify because basic block has already been generated";

    if (falseTarget.isPresent()) {
      assert trueTarget.isPresent() : "false target exists without matching true target";

      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), new LLBasicBlock());
        final LLBasicBlock simplifiedBB = getTrueTarget().simplify(simplified, exits, exceptions, unreachableCodeElimination);
        simplified.get(getTrueTarget()).subsume(simplifiedBB);
        if (exits.contains(simplifiedBB)) {
          exits.add(simplified.get(getTrueTarget()));
        }
        if (exceptions.contains(simplifiedBB)) {
          exceptions.add(simplified.get(getTrueTarget()));
        }
      }

      if (!simplified.containsKey(getFalseTarget())) {
        simplified.put(getFalseTarget(), new LLBasicBlock());
        final LLBasicBlock simplifiedBB = getFalseTarget().simplify(simplified, exits, exceptions, unreachableCodeElimination);
        simplified.get(getFalseTarget()).subsume(simplifiedBB);
        if (exits.contains(simplifiedBB)) {
          exits.add(simplified.get(getFalseTarget()));
        }
        if (exceptions.contains(simplifiedBB)) {
          exceptions.add(simplified.get(getFalseTarget()));
        }
      }

      final LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());
      final LLBasicBlock simplifiedFalseTarget = simplified.get(getFalseTarget());

      if (unreachableCodeElimination) {
        if (instructions.size() < 1) {
          throw new RuntimeException("should have at least one instruction (a comparison)");
        }

        if (instructions.get(instructions.size() - 1) instanceof LLCompare comparison) {
          switch (evaluateComparison(comparison)) {
            case ALWAYS_TRUE: {
              if (getTrueTarget().predecessors.size() == 1) {
                final List<LLInstruction> resultInstructions = new ArrayList<>(instructions.subList(0, instructions.size() - 1));
                resultInstructions.addAll(simplifiedTrueTarget.instructions);

                final LLBasicBlock simplifiedBB = new LLBasicBlock(resultInstructions);

                if (exits.contains(simplifiedTrueTarget)) {
                  exits.add(simplifiedBB);
                }

                if (exceptions.contains(simplifiedTrueTarget)) {
                  exceptions.add(simplifiedBB);
                }

                if (simplifiedTrueTarget.hasTrueTarget()) {
                  // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                  simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget.getTrueTarget()));
                }

                if (simplifiedTrueTarget.hasFalseTarget()) {
                  // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                  simplifiedBB.setFalseTarget(getNextNonEmpty(simplifiedTrueTarget.getFalseTarget()));
                }

                return simplifiedBB;
              } else {
                final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions.subList(0, instructions.size() - 1));

                // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget));

                return simplifiedBB;
              }
            } case ALWAYS_FALSE: {
              if (getFalseTarget().predecessors.size() == 1) {
                final List<LLInstruction> resultInstructions = new ArrayList<>(instructions.subList(0, instructions.size() - 1));
                resultInstructions.addAll(simplifiedFalseTarget.instructions);

                final LLBasicBlock simplifiedBB = new LLBasicBlock(resultInstructions);

                if (exits.contains(simplifiedFalseTarget)) {
                  exits.add(simplifiedBB);
                }

                if (exceptions.contains(simplifiedFalseTarget)) {
                  exceptions.add(simplifiedBB);
                }

                if (simplifiedFalseTarget.hasTrueTarget()) {
                  // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                  simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedFalseTarget.getTrueTarget()));
                }

                if (simplifiedFalseTarget.hasFalseTarget()) {
                  // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                  simplifiedBB.setFalseTarget(getNextNonEmpty(simplifiedFalseTarget.getFalseTarget()));
                }

                return simplifiedBB;
              } else {
                final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions.subList(0, instructions.size() - 1));

                // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
                simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedFalseTarget));

                return simplifiedBB;
              }
            } case AMBIGUOUS: {
              final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions);

              // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
              simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget));
              simplifiedBB.setFalseTarget(getNextNonEmpty(simplifiedFalseTarget));

              return simplifiedBB;
            } default: {
              throw new RuntimeException("unreachable");
            }
          }
        } else {
          throw new RuntimeException("expected last instruction to be a comparison");
        }
      } else {
        final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions);

        // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
        simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget));
        simplifiedBB.setFalseTarget(getNextNonEmpty(simplifiedFalseTarget));

        return simplifiedBB;
      }
    } else if (trueTarget.isPresent()) {
      if (!simplified.containsKey(getTrueTarget())) {
        simplified.put(getTrueTarget(), new LLBasicBlock());
        final LLBasicBlock simplifiedBB = getTrueTarget().simplify(simplified, exits, exceptions, unreachableCodeElimination);
        simplified.get(getTrueTarget()).subsume(simplifiedBB);
        if (exits.contains(simplifiedBB)) {
          exits.add(simplified.get(getTrueTarget()));
        }
        if (exceptions.contains(simplifiedBB)) {
          exceptions.add(simplified.get(getTrueTarget()));
        }
      }

      final LLBasicBlock simplifiedTrueTarget = simplified.get(getTrueTarget());

      if (getTrueTarget().predecessors.size() == 1) {
        final List<LLInstruction> resultInstructions = new ArrayList<>(instructions);
        resultInstructions.addAll(simplifiedTrueTarget.instructions);

        final LLBasicBlock simplifiedBB = new LLBasicBlock(resultInstructions);

        if (exits.contains(simplifiedTrueTarget)) {
          exits.add(simplifiedBB);
        }

        if (exceptions.contains(simplifiedTrueTarget)) {
          exceptions.add(simplifiedBB);
        }

        if (simplifiedTrueTarget.hasTrueTarget()) {
          // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
          simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget.getTrueTarget()));
        }

        if (simplifiedTrueTarget.hasFalseTarget()) {
          // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
          simplifiedBB.setFalseTarget(getNextNonEmpty(simplifiedTrueTarget.getFalseTarget()));
        }

        return simplifiedBB;
      } else {
        final LLBasicBlock simplifiedBB = new LLBasicBlock(instructions);

        // NOTE(rbd): Do not set back edges yet, most of these blocks will disappear after simplification.
        simplifiedBB.setTrueTarget(getNextNonEmpty(simplifiedTrueTarget));

        return simplifiedBB;
      }
    } else {
      return this;
    }
  } */

  public int getIndex() {
    return index;
  }

  // NOTE(rbd): This is very unfortunate, I know... This is necessary to handle loops in CFG simplification above.
  /* private void subsume(LLBasicBlock that) {
    this.index = that.index;
    this.instructions = that.instructions;
    this.trueTarget = that.trueTarget;
    this.falseTarget = that.falseTarget;
    this.generated = that.generated;
  } */

  private boolean canSimplify(boolean unreachableCodeElimination) {
    return !hasFalseTarget()
      && hasTrueTarget()
      && (getTrueTarget().getPredecessors().size() == 1);
  }

  public Optional<LLBasicBlock> simplify(Optional<LLBasicBlock> exit, final Set<LLBasicBlock> exceptions, boolean unreachableCodeElimination) {
    while (canSimplify(unreachableCodeElimination)) {
      final LLBasicBlock next = getTrueTarget();

      instructions.addAll(next.getInstructions());

      trueTarget = next.trueTarget;
      falseTarget = next.falseTarget;

      if (hasFalseTarget()) {
        final LLBasicBlock trueTarget = getTrueTarget();
        trueTarget.predecessors.remove(next);
        trueTarget.predecessors.add(this);

        final LLBasicBlock falseTarget = getFalseTarget();
        falseTarget.predecessors.remove(next);
        falseTarget.predecessors.add(this);
      } else if (hasTrueTarget()) {
        final LLBasicBlock trueTarget = getTrueTarget();
        trueTarget.predecessors.remove(next);
        trueTarget.predecessors.add(this);
      } else {
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
