package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.common.*;
import edu.mit.compilers.ll.*;

public class UnreachableCodeElimination implements Optimization {

  private enum ComparisonResult {
    ALWAYS_TRUE,
    ALWAYS_FALSE,
    AMBIGUOUS;

    public static ComparisonResult fromBoolean(boolean x) {
      return x ? ALWAYS_TRUE : ALWAYS_FALSE;
    }
  }

  private static ComparisonResult evaluateComparison(LLCompare comparison) {
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
  }

  public void apply(final LLMethodDeclaration methodDeclaration, final LLControlFlowGraph controlFlowGraph, final List<LLDeclaration> globals) {
    final Stack<LLBasicBlock> toVisit = new Stack<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    toVisit.push(controlFlowGraph.getEntry());

    while (!toVisit.isEmpty()) {
      final LLBasicBlock current = toVisit.pop();

      if (!visited.contains(current)) {
        if (current.hasFalseTarget()) {
          final List<LLInstruction> instructions = current.getInstructions();

          assert instructions.size() > 0 : "expected at least one instruction (comparison)";

          if (instructions.get(instructions.size() - 1) instanceof LLCompare comparison) {
            switch (evaluateComparison(comparison)) {
              case ALWAYS_TRUE:
                instructions.remove(instructions.size() - 1);
                // NOTE(rbd): Don't care about predecessors, they are fixed in LLControlFlowGraph.simplify().
                current.clearFalseTarget();
                break;
              case ALWAYS_FALSE:
                instructions.remove(instructions.size() - 1);
                // NOTE(rbd): Don't care about predecessors, they are fixed in LLControlFlowGraph.simplify().
                current.replaceTrueTarget(current.getFalseTarget());
                current.clearFalseTarget();
                break;
              case AMBIGUOUS:
                break;
              default:
                throw new RuntimeException("unreachable");
            }
          } else {
            throw new RuntimeException("expected last instruction to be a comparison");
          }
        }

        if (current.hasTrueTarget()) {
          toVisit.push(current.getTrueTarget());
        }

        if (current.hasFalseTarget()) {
          toVisit.push(current.getFalseTarget());
        }

        visited.add(current);
      }
    }

    controlFlowGraph.simplify();
  }

}
