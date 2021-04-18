package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class CopyPropagation implements Optimization {

  private final boolean constantFolding;
  private final List<LLInstruction> instructions = new ArrayList<>();

  public CopyPropagation(final boolean constantFolding) {
    this.constantFolding = constantFolding;
  }

  private static LLInstruction evaluateBinary(LLBinary binary, LLConstantDeclaration leftConstant, LLConstantDeclaration rightConstant) {
    if (binary.getType() == BinaryExpressionType.OR) {
      return new LLIntegerLiteral((leftConstant.getValue() == 1 || rightConstant.getValue() == 1) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.AND) {
      return new LLIntegerLiteral((leftConstant.getValue() == 1 && rightConstant.getValue() == 1) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.EQUAL) {
      return new LLIntegerLiteral((leftConstant.getValue() == rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.NOT_EQUAL) {
      return new LLIntegerLiteral((leftConstant.getValue() != rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.LESS_THAN) {
      return new LLIntegerLiteral((leftConstant.getValue() < rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.LESS_THAN_OR_EQUAL) {
      return new LLIntegerLiteral((leftConstant.getValue() <= rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.GREATER_THAN) {
      return new LLIntegerLiteral((leftConstant.getValue() > rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.GREATER_THAN_OR_EQUAL) {
      return new LLIntegerLiteral((leftConstant.getValue() >= rightConstant.getValue()) ? 1 : 0, binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.ADD) {
      return new LLIntegerLiteral(leftConstant.getValue() + rightConstant.getValue(), binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.SUBTRACT) {
      return new LLIntegerLiteral(leftConstant.getValue() - rightConstant.getValue(), binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.MULTIPLY) {
      return new LLIntegerLiteral(leftConstant.getValue() * rightConstant.getValue(), binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.DIVIDE) {
      if (rightConstant.getValue() == 0) {
        return new LLException(LLException.Type.DivideByZero);
      } else {
        return new LLIntegerLiteral(leftConstant.getValue() / rightConstant.getValue(), binary.getResult());
      }
    } else if (binary.getType() == BinaryExpressionType.MODULUS) {
      if (rightConstant.getValue() == 0) {
        return new LLException(LLException.Type.DivideByZero);
      } else {
        return new LLIntegerLiteral(leftConstant.getValue() % rightConstant.getValue(), binary.getResult());
      }
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static LLIntegerLiteral evaluateUnary(LLUnary unary, LLConstantDeclaration constant) {
    if (unary.getType() == UnaryExpressionType.NOT) {
      return new LLIntegerLiteral((constant.getValue() == 1) ? 0 : 1, unary.getResult());
    } else if (unary.getType() == UnaryExpressionType.NEGATE) {
      return new LLIntegerLiteral(-constant.getValue(), unary.getResult());
    } else if (unary.getType() == UnaryExpressionType.INCREMENT) {
      return new LLIntegerLiteral(constant.getValue() + 1, unary.getResult());
    } else if (unary.getType() == UnaryExpressionType.DECREMENT) {
      return new LLIntegerLiteral(constant.getValue() - 1, unary.getResult());
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private Map<LLDeclaration, Set<Integer>> getDefinitionIndices(final BitSet entry) {
    final Map<LLDeclaration, Set<Integer>> definitionIndices = new HashMap<>();

    for (int i = entry.nextSetBit(0); i != -1; i = entry.nextSetBit(i + 1)) {
      final LLInstruction definitionInstruction = instructions.get(i);

      assert definitionInstruction.definition().isPresent() : "should only contain definitions";

      final LLDeclaration definition = definitionInstruction.definition().get();

      if (!definitionIndices.containsKey(definition)) {
        definitionIndices.put(definition, new HashSet<>());
      }

      definitionIndices.get(definition).add(i);
    }

    return definitionIndices;
  }

  private boolean update(final LLBasicBlock block, final int index, final BitSet entry, final BitSet exit, final Set<LLDeclaration> globals) {
    final Map<LLDeclaration, Set<Integer>> definitionIndices = getDefinitionIndices(entry);
    final BitSet current = new BitSet();
    current.or(entry);

    for (int i = index; i < index + block.getInstructions().size(); i++) {
      final LLInstruction instruction = instructions.get(i);

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (definitionIndices.containsKey(definition)) {
          for (final int j : definitionIndices.get(definition)) {
            current.clear(j);
          }
        }

        current.set(i);
        definitionIndices.put(definition, new HashSet<>(Set.of(i)));
      }
    }

    if (current.equals(exit)) {
      return false;
    } else {
      exit.clear();
      exit.or(current);
      return true;
    }
  }

  private boolean transform(final LLBasicBlock block, final int index, final BitSet entry, final BitSet exit, final Set<LLDeclaration> globals) {
    final Map<LLDeclaration, Set<Integer>> definitionIndices = getDefinitionIndices(entry);
    final List<LLInstruction> newInstructions = new ArrayList<>();
    boolean instructionsChanged = false;

    for (int i = index; i < index + block.getInstructions().size(); i++) {
      final LLInstruction instruction = instructions.get(i);
      final List<LLDeclaration> newUses = new ArrayList<>();
      boolean usesChanged = false;

      for (final LLDeclaration use : instruction.uses()) {
        if (!globals.contains(use) && definitionIndices.containsKey(use) && definitionIndices.get(use).size() == 1) {
          final int j = definitionIndices.get(use).iterator().next();
          final LLInstruction definitionInstruction = instructions.get(j);

          if (definitionInstruction instanceof LLIntegerLiteral integerLiteral) {
            usesChanged = true;
            newUses.add(new LLConstantDeclaration(integerLiteral.getValue()));
          } else if (definitionInstruction instanceof LLLength length) {
            usesChanged = true;
            newUses.add(new LLConstantDeclaration(length.getDeclaration().getLength()));
          } else if (definitionInstruction instanceof LLStringLiteral
              || definitionInstruction instanceof LLStoreScalar
              || definitionInstruction instanceof LLLoadScalar
              || definitionInstruction instanceof LLCopy) {
            usesChanged = true;
            newUses.add(definitionInstruction.uses().iterator().next());
          } else {
            newUses.add(use);
          }
        } else {
          newUses.add(use);
        }
      }

      LLInstruction newInstruction;

      if (usesChanged) {
        newInstruction = instruction.usesReplaced(newUses);
        instructionsChanged = true;
      } else {
        newInstruction = instruction;
      }

      if (constantFolding) {
        if (newInstruction instanceof LLBinary binary) {
          if (binary.getLeft() instanceof LLConstantDeclaration left 
              && binary.getRight() instanceof LLConstantDeclaration right) {
            instructionsChanged = true;
            newInstruction = evaluateBinary(binary, left, right);
          }
        } else if (newInstruction instanceof LLUnary unary) {
          if (unary.getExpression() instanceof LLConstantDeclaration expression) {
            instructionsChanged = true;
            newInstruction = evaluateUnary(unary, expression);
          }
        }
      }

      instructions.set(i, newInstruction);
      newInstructions.add(newInstruction);

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        definitionIndices.put(definition, new HashSet<>(Set.of(i)));
      }
    }

    if (instructionsChanged) {
      block.setInstructions(newInstructions);
      return true;
    } else {
      return false;
    }
  }

  public void apply(final LLMethodDeclaration methodDeclaration, final LLControlFlowGraph controlFlowGraph, final List<LLDeclaration> globals) {
    instructions.clear();

    final Map<LLBasicBlock, Integer> indices = new HashMap<>();
    final Map<LLBasicBlock, BitSet> entries = new HashMap<>();
    final Map<LLBasicBlock, BitSet> exits = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    workSet.add(controlFlowGraph.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        indices.put(block, instructions.size());
        entries.put(block, new BitSet());
        exits.put(block, new BitSet());

        instructions.addAll(block.getInstructions());

        workSet.addAll(block.getSuccessors());
        visited.add(block);
      }
    }

    workSet.addAll(visited);

    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (update(block, indices.get(block), entries.get(block), exits.get(block), new HashSet<>(globals))) {
        for (LLBasicBlock successor : block.getSuccessors()) {
          entries.get(successor).or(exits.get(block));

          workSet.add(successor);
        }
      }
    }

    boolean changed;

    do {
      changed = false;
      for (final LLBasicBlock block : visited) {
        changed |= transform(block, indices.get(block), entries.get(block), exits.get(block), new HashSet<>(globals));
      }
    } while (changed);
  }

}
