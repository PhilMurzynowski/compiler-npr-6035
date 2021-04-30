package edu.mit.compilers.ll;

import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Optional;

import static edu.mit.compilers.common.Utilities.*;

public class LLControlFlowGraph implements LLNode {

  private final LLBasicBlock entry;
  private Optional<LLBasicBlock> exit;
  private final Set<LLBasicBlock> exceptions;

  private LLControlFlowGraph(LLBasicBlock entry, Optional<LLBasicBlock> exit, Set<LLBasicBlock> exceptions) {
    this.entry = entry;
    this.exit = exit;
    this.exceptions = new HashSet<>(exceptions);
  }

  public LLControlFlowGraph(LLBasicBlock entry, LLBasicBlock exit) {
    this(entry, Optional.of(exit), Set.of());
  }

  public static LLControlFlowGraph single(LLBasicBlock basicBlock) {
    return new LLControlFlowGraph(basicBlock, basicBlock);
  }

  public static LLControlFlowGraph empty() {
    return single(new LLBasicBlock());
  }

  public LLBasicBlock getEntry() {
    return entry;
  }

  public LLBasicBlock expectExit() {
    if (exit.isEmpty()) {
      throw new RuntimeException("exit does not exist. this suggests that UnreachableCodeElimination has been run already.");
    } else {
      return exit.get();
    }
  }

  public boolean hasExit() {
    return exit.isPresent();
  }

  public Set<LLBasicBlock> getExceptions() {
    return exceptions;
  }

  public LLControlFlowGraph concatenate(LLControlFlowGraph that) {
    LLBasicBlock.setTrueTarget(this.expectExit(), that.entry);
    return new LLControlFlowGraph(this.entry, that.expectExit());
  }

  public LLControlFlowGraph concatenate(LLBasicBlock basicBlock) {
    return concatenate(LLControlFlowGraph.single(basicBlock));
  }

  public LLControlFlowGraph concatenate(LLInstruction ...instructions) {
    return concatenate(LLControlFlowGraph.single(new LLBasicBlock(instructions)));
  }

  public void addException(LLBasicBlock exception) {
    exceptions.add(exception);
  }

  public void simplify() {
    final Stack<LLBasicBlock> toVisit = new Stack<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    toVisit.push(entry);

    // Merge linear CFG
    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        exit = current.merge(exit, exceptions);

        if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    visited.clear();
    toVisit.push(entry);

    // Remove indirect branching
    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasTrueTarget()) {
          LLBasicBlock next = current.getTrueTarget();

          while (next.getInstructions().size() == 0 && next.hasTrueTarget()) {
            next = next.getTrueTarget();
          }

          // NOTE(rbd): Don't care about predecessors, they are fixed below.
          LLBasicBlock.replaceTrueTarget(current, next);

          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          LLBasicBlock next = current.getFalseTarget();

          while (next.getInstructions().size() == 0 && next.hasTrueTarget()) {
            next = next.getTrueTarget();
          }

          // NOTE(rbd): Don't care about predecessors, they are fixed below.
          LLBasicBlock.replaceFalseTarget(current, next);

          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    visited.clear();
    toVisit.push(entry);

    // Clear predecessors
    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        current.clearPredecessors();

        if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    visited.clear();
    toVisit.push(entry);

    // Set predecessors
    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasTrueTarget()) {
          current.getTrueTarget().addPredecessor(current);
          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          current.getFalseTarget().addPredecessor(current);
          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    final Set<LLBasicBlock> simplifiedExits = new HashSet<>();
    final Set<LLBasicBlock> simplifiedExceptions = new HashSet<>();

    visited.clear();
    toVisit.push(entry);

    // Remove unreachable exits
    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {

        if (current.hasFalseTarget()) {
          toVisit.push(current.getTrueTarget());
          toVisit.push(current.getFalseTarget());
        } else if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        } else {
          if (exceptions.contains(current)) {
            simplifiedExceptions.add(current);
          } else if (exit.isPresent() && exit.get() == current) {
            simplifiedExits.add(current);
          } else {
            throw new RuntimeException("block with no true or false targets should be exit or exception");
          }
        }

        visited.add(current);
      }
    }

    if (simplifiedExits.size() == 0) {
      assert simplifiedExceptions.size() > 0 : "if there is no exit, there should be at least one exception";
      assert simplifiedExceptions.size() <= exceptions.size() : "after simplification, should have at most hte number of exceptions from before simplification";

      exit = Optional.empty();
      exceptions.clear();
      exceptions.addAll(simplifiedExceptions);
    } else if (simplifiedExits.size() == 1) {
      assert simplifiedExceptions.size() <= exceptions.size() : "after simplification, should have at most hte number of exceptions from before simplification";

      exit = Optional.of(simplifiedExits.iterator().next());
      exceptions.clear();
      exceptions.addAll(simplifiedExceptions);
    } else {
      throw new RuntimeException("too many exits");
    }
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    final Set<LLBasicBlock> visited = new HashSet<>();
    final Stack<LLBasicBlock> toVisit = new Stack<>();

    toVisit.push(entry);

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        s.append(indent(depth) + current.prettyStringDeclaration(depth) + "\n");

        if (current.hasFalseTarget()) {
          toVisit.push(current.getFalseTarget());
        }

        if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        }

        visited.add(current);
      }
    }
    
    return s.toString().strip();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLControlFlowGraph {\n");
    s.append(indent(depth + 1) + "entry: " + entry.getIndex() + ",\n");
    if (exit.isPresent()) {
      s.append(indent(depth + 1) + "exit: " + exit.get().getIndex() + ",\n");
    }
    s.append(indent(depth + 1) + "basicBlocks: [\n");

    final Set<LLBasicBlock> visited = new HashSet<>();
    final Stack<LLBasicBlock> toVisit = new Stack<>();

    toVisit.push(entry);

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        s.append(indent(depth + 2) + current.debugString(depth + 2) + ",\n");

        if (current.hasFalseTarget()) {
          toVisit.push(current.getFalseTarget());
        }

        if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        }

        visited.add(current);
      }
    }

    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // NOTE(rbd): LLControlFlowGraph is mutable. Use default `.equals()` and `.hashCode()`

  // @Override
  // public boolean equals(Object that) {
  //   throw new RuntimeException("not implemented");
  // }

  // @Override
  // public int hashCode() {
  //   throw new RuntimeException("not implemented");
  // }

}
