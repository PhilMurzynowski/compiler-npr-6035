package edu.mit.compilers.ll;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
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
    this.exit.setTrueTarget(that.entry);
    return new LLControlFlowGraph(this.entry, that.exit);
  }

  public LLControlFlowGraph concatenate(LLBasicBlock basicBlock) {
    return concatenate(LLControlFlowGraph.single(basicBlock));
  }

  public LLControlFlowGraph concatenate(LLInstruction ...instructions) {
    return concatenate(LLControlFlowGraph.single(new LLBasicBlock(instructions)));
  }

  public LLControlFlowGraph simplify() {
    final Map<LLBasicBlock, Set<LLBasicBlock>> backEdges = new HashMap<>();
    final Set<LLBasicBlock> visited = new HashSet<>();
    final Stack<LLBasicBlock> toVisit = new Stack<>();

    toVisit.push(entry);

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasTrueTarget()) {
          if (!backEdges.containsKey(current.getTrueTarget())) {
            backEdges.put(current.getTrueTarget(), new HashSet<>());
          }
          backEdges.get(current.getTrueTarget()).add(current);

          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          if (!backEdges.containsKey(current.getFalseTarget())) {
            backEdges.put(current.getFalseTarget(), new HashSet<>());
          }
          backEdges.get(current.getFalseTarget()).add(current);

          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    LLBasicBlock simplifiedEntry = entry.simplify(backEdges, new HashMap<>());

    final Set<LLBasicBlock> simplifiedExits = new HashSet<>();
    visited.clear();

    toVisit.push(simplifiedEntry);

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasFalseTarget()) {
          toVisit.push(current.getTrueTarget());
          toVisit.push(current.getFalseTarget());
        } else if (current.hasTrueTarget()) {
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

}
