package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ConstantFolding implements Optimization {

  private static LLConstantDeclaration evaluateBinary(LLBinary binary, LLConstantDeclaration leftConstant, LLConstantDeclaration rightConstant) {
    if (binary.getType() == BinaryExpressionType.OR) {
      return new LLConstantDeclaration((leftConstant.getValue() == 1 || rightConstant.getValue() == 1) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.AND) {
      return new LLConstantDeclaration((leftConstant.getValue() == 1 && rightConstant.getValue() == 1) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.EQUAL) {
      return new LLConstantDeclaration((leftConstant.getValue() == rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.NOT_EQUAL) {
      return new LLConstantDeclaration((leftConstant.getValue() != rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.LESS_THAN) {
      return new LLConstantDeclaration((leftConstant.getValue() < rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.LESS_THAN_OR_EQUAL) {
      return new LLConstantDeclaration((leftConstant.getValue() <= rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.GREATER_THAN) {
      return new LLConstantDeclaration((leftConstant.getValue() > rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.GREATER_THAN_OR_EQUAL) {
      return new LLConstantDeclaration((leftConstant.getValue() >= rightConstant.getValue()) ? 1 : 0);
    } else if (binary.getType() == BinaryExpressionType.ADD) {
      return new LLConstantDeclaration(leftConstant.getValue() + rightConstant.getValue());
    } else if (binary.getType() == BinaryExpressionType.SUBTRACT) {
      return new LLConstantDeclaration(leftConstant.getValue() - rightConstant.getValue());
    } else if (binary.getType() == BinaryExpressionType.MULTIPLY) {
      return new LLConstantDeclaration(leftConstant.getValue() * rightConstant.getValue());
    } else if (binary.getType() == BinaryExpressionType.DIVIDE) {
      return new LLConstantDeclaration(leftConstant.getValue() / rightConstant.getValue());
    } else if (binary.getType() == BinaryExpressionType.MODULUS) {
      return new LLConstantDeclaration(leftConstant.getValue() % rightConstant.getValue());
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static LLConstantDeclaration evaluateUnary(LLUnary unary, LLConstantDeclaration constant) {
    if (unary.getType() == UnaryExpressionType.NOT) {
      return new LLConstantDeclaration((constant.getValue() == 1) ? 0 : 1);
    } else if (unary.getType() == UnaryExpressionType.NEGATE) {
      return new LLConstantDeclaration(-constant.getValue());
    } else if (unary.getType() == UnaryExpressionType.INCREMENT) {
      return new LLConstantDeclaration(constant.getValue() + 1);
    } else if (unary.getType() == UnaryExpressionType.DECREMENT) {
      return new LLConstantDeclaration(constant.getValue() - 1);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static LLDeclaration propagate(LLDeclaration use, Map<LLDeclaration, Set<LLInstruction>> definitionInstructions, Set<LLDeclaration> globals, Set<LLDeclaration> visited) throws CycleDetectedException {
    if (visited.contains(use)) {
      throw new CycleDetectedException("cycle detected");
    }

    if (!globals.contains(use) && definitionInstructions.containsKey(use) && definitionInstructions.get(use).size() == 1) {
      final LLInstruction definitionInstruction = definitionInstructions.get(use).iterator().next();
      if (definitionInstruction instanceof LLIntegerLiteral integerLiteral) {
        return new LLConstantDeclaration(integerLiteral.getValue());
      } else if (definitionInstruction instanceof LLLength length) {
        return new LLConstantDeclaration(length.getDeclaration().getLength());
      } else if (definitionInstruction instanceof LLStringLiteral stringLiteral) {
        return stringLiteral.getDeclaration();
      } else if (definitionInstruction instanceof LLStoreScalar storeScalar) {
        visited.add(use);
        return propagate(storeScalar.uses().iterator().next(), definitionInstructions, globals, visited);
      } else if (definitionInstruction instanceof LLLoadScalar loadScalar) {
        visited.add(use);
        return propagate(loadScalar.uses().iterator().next(), definitionInstructions, globals, visited);
      } else if (definitionInstruction instanceof LLCopy copy) {
        visited.add(use);
        return propagate(copy.uses().iterator().next(), definitionInstructions, globals, visited);
      } else if (definitionInstruction instanceof LLBinary binary) {
        visited.add(use);
        final LLDeclaration left = propagate(binary.getLeft(), definitionInstructions, globals, visited);
        final LLDeclaration right = propagate(binary.getRight(), definitionInstructions, globals, visited);
        if (left instanceof LLConstantDeclaration leftConstant && right instanceof LLConstantDeclaration rightConstant) {
          return evaluateBinary(binary, leftConstant, rightConstant);
        } else {
          return use;
        }
      } else if (definitionInstruction instanceof LLUnary unary) {
        visited.add(use);
        final LLDeclaration expression = propagate(unary.getExpression(), definitionInstructions, globals, visited);
        if (expression instanceof LLConstantDeclaration constant) {
          return evaluateUnary(unary, constant);
        } else {
          return use;
        }
      } else {
        return use;
      }
    } else {
      return use;
    }
  }

  private static boolean update(LLBasicBlock basicBlock, BitMap<LLInstruction> entry, BitMap<LLInstruction> exit, boolean propagate, Set<LLDeclaration> globals) {
    final Map<LLDeclaration, Set<LLInstruction>> definitionInstructions = new HashMap<>();

    for (LLInstruction definitionInstruction : entry.trueSet()) {
      assert definitionInstruction.definition().isPresent() : "should only contain definitions";

      final LLDeclaration definition = definitionInstruction.definition().get();

      if (!definitionInstructions.containsKey(definition)) {
        definitionInstructions.put(definition, new HashSet<>());
      }

      definitionInstructions.get(definition).add(definitionInstruction);
    }

    final BitMap<LLInstruction> current = new BitMap<>(entry);
    final List<LLInstruction> newInstructions = new ArrayList<>();

    for (LLInstruction instruction : basicBlock.getInstructions()) {
      LLInstruction newInstruction = null; // NOTE(rbd): This will always be initialized before use, but Java doesn't believe me...

      if (propagate) {
        final List<LLDeclaration> newUses = new ArrayList<>();

        for (LLDeclaration use : instruction.uses()) {
          final Set<LLDeclaration> cycle = new LinkedHashSet<>();
          try {
            newUses.add(propagate(use, definitionInstructions, globals, cycle));
          } catch (CycleDetectedException e) {
            System.err.println("WARN(rbd): Cycle detected:\n");
            for (LLDeclaration declaration : cycle) {
              assert definitionInstructions.get(declaration).size() == 1 : "should only be able to form a cycle with definitions from single instructions";
              System.err.println(indent(1) + definitionInstructions.get(declaration).iterator().next().prettyString(1));
            }
            System.err.println("\nIt is possible this wasn't handled optimally.");

            newUses.add(use);
            // throw new RuntimeException("cycle detected");
          }
        }

        newInstruction = instruction.usesReplaced(newUses);
        newInstructions.add(newInstruction);
      }

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (definitionInstructions.containsKey(definition)) {
          for (LLInstruction definitionInstruction : definitionInstructions.get(definition)) {
            current.clear(definitionInstruction);
          }
        }

        if (propagate) {
          current.set(newInstruction);
          definitionInstructions.put(definition, new HashSet<>(Set.of(newInstruction)));
        } else {
          current.set(instruction);
          definitionInstructions.put(definition, new HashSet<>(Set.of(instruction)));
        }
      }
    }

    if (propagate) {
      basicBlock.setInstructions(newInstructions);
    }

    if (current.sameValue(exit)) {
      return false;
    } else {
      exit.subsume(current);
      return true;
    }
  }

  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    final Map<LLBasicBlock, BitMap<LLInstruction>> entries = new HashMap<>();
    final Map<LLBasicBlock, BitMap<LLInstruction>> exits = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    workSet.add(controlFlowGraph.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        entries.put(block, new BitMap<>());
        exits.put(block, new BitMap<>());

        workSet.addAll(block.getSuccessors());

        visited.add(block);
      }
    }

    workSet.addAll(visited);

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (update(block, entries.get(block), exits.get(block), false, new HashSet<>(globals))) {
        for (LLBasicBlock successor : block.getSuccessors()) {
          entries.get(successor).or(exits.get(block));

          workSet.add(successor);
        }
      }
    }

    for (LLBasicBlock block : visited) {
      update(block, entries.get(block), exits.get(block), true, new HashSet<>(globals));
    }
  }

}
