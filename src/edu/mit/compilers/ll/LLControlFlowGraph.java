package edu.mit.compilers.ll;

import java.util.Set;
import java.util.HashSet;
// import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import static edu.mit.compilers.common.Utilities.indent;

public class LLControlFlowGraph implements LLNode {

  private final LLBasicBlock entry;
  private final LLBasicBlock exit;

  // NOTE(rbd): Please try not to add anything to this class. Let me know if you do. :)

  public LLControlFlowGraph(LLBasicBlock entry, LLBasicBlock exit) {
    this.entry = entry;
    this.exit = exit;
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

  public LLControlFlowGraph simplify() {
    LLBasicBlock simplifiedEntry = entry.simplify(new HashMap<>());

    final Set<LLBasicBlock> visited = new HashSet<>();
    final Stack<LLBasicBlock> toVisit = new Stack<>();
    final Set<LLBasicBlock> simplifiedExits = new HashSet<>();

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
          simplifiedExits.add(current);
        }

        visited.add(current);
      }
    }

    assert simplifiedExits.size() == 1 : "more than one exit";

    return new LLControlFlowGraph(simplifiedEntry, simplifiedExits.iterator().next());
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
