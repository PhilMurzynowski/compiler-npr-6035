package edu.mit.compilers.ll;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;

import static edu.mit.compilers.common.Utilities.*;

public class LLControlFlowGraph implements LLNode {

  private final LLBasicBlock entry;
  private final LLBasicBlock exit;
  private final Set<LLBasicBlock> exceptions;

  private LLControlFlowGraph(LLBasicBlock entry, LLBasicBlock exit, Set<LLBasicBlock> exceptions) {
    this.entry = entry;
    this.exit = exit;
    this.exceptions = new HashSet<>(exceptions);
  }

  public LLControlFlowGraph(LLBasicBlock entry, LLBasicBlock exit) {
    this(entry, exit, Set.of());
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

  public LLBasicBlock getExit() {
    return exit;
  }

  public Set<LLBasicBlock> getExceptions() {
    return exceptions;
  }

  public LLControlFlowGraph concatenate(LLControlFlowGraph that) {
    LLBasicBlock.setTrueTarget(this.exit, that.entry);
    return new LLControlFlowGraph(this.entry, that.exit);
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

  public LLControlFlowGraph simplify() {
    final Set<LLBasicBlock> allExits = new HashSet<>(Set.of(exit));
    final Set<LLBasicBlock> allExceptions = new HashSet<>(exceptions);
    LLBasicBlock simplifiedEntry = entry.simplify(new HashMap<>(), allExits, allExceptions);

    final Set<LLBasicBlock> visited = new HashSet<>();
    final Stack<LLBasicBlock> toVisit = new Stack<>();
    final Set<LLBasicBlock> simplifiedExits = new HashSet<>();
    final Set<LLBasicBlock> simplifiedExceptions = new HashSet<>();

    toVisit.push(simplifiedEntry);

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasFalseTarget()) {
          current.getTrueTarget().addPredecessor(current);
          current.getFalseTarget().addPredecessor(current);

          toVisit.push(current.getTrueTarget());
          toVisit.push(current.getFalseTarget());
        } else if (current.hasTrueTarget()) {
          current.getTrueTarget().addPredecessor(current);

          toVisit.push(current.getTrueTarget());
        } else {
          assert allExits.contains(current) || allExceptions.contains(current) : "block with no true/false target should be in either exits or exceptions";

          if (allExits.contains(current)) {
            simplifiedExits.add(current);
          } else if (allExceptions.contains(current)) {
            simplifiedExceptions.add(current);
          }
        }

        visited.add(current);
      }
    }

    assert simplifiedExits.size() == 1 : "more than one exit";
    assert simplifiedExceptions.size() <= exceptions.size() : "after simplification, should have at most the number of exceptions from before simplification";

    return new LLControlFlowGraph(simplifiedEntry, simplifiedExits.iterator().next(), simplifiedExceptions);
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
    s.append(indent(depth + 1) + "exit: " + exit.getIndex() + ",\n");
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
