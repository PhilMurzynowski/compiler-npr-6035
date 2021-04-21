package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class CopyPropagation implements Optimization {

  private final boolean constantFolding;
  private final boolean algebraicSimplification;
  private final List<LLInstruction> instructions = new ArrayList<>();
  private final Map<LLDeclaration, Integer> identityIndices = new HashMap<>();

  public CopyPropagation(final boolean constantFolding, final boolean algebraicSimplification) {
    this.constantFolding = constantFolding;
    this.algebraicSimplification = algebraicSimplification;
  }

  private static boolean isPowerOf2(long x) {
    return (x & (x - 1)) == 0;
  }
  private static long floorLog2(long x) {
    return 63 - Long.numberOfLeadingZeros(x);
  }

  private static Optional<LLInstruction> trySimplifyBinary(LLBinary binary) {
    if (binary.getType() == BinaryExpressionType.OR) {
      if (binary.getLeft() instanceof LLConstantDeclaration orLeft) {
        if (orLeft.getValue() == 1) { // true || a => true
          return Optional.of(new LLIntegerLiteral(1, binary.getResult()));
        } else if (orLeft.getValue() == 0) { // false || a => a
          return Optional.of(new LLCopy(binary.getRight(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration orRight) {
        if (orRight.getValue() == 1) { // a || true => true
          return Optional.of(new LLIntegerLiteral(1 , binary.getResult()));
        } else if (orRight.getValue() == 0) { // a || false => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.AND) {
      if (binary.getLeft() instanceof LLConstantDeclaration andLeft) {
        if (andLeft.getValue() == 1) { // true && a => a
          return Optional.of(new LLCopy(binary.getRight(), binary.getResult()));
        } else if (andLeft.getValue() == 0) { // false && a => false
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration andRight) {
        if (andRight.getValue() == 1) { // a && true => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else if (andRight.getValue() == 0) { // a && false => false
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.EQUAL
        || binary.getType() == BinaryExpressionType.NOT_EQUAL
        || binary.getType() == BinaryExpressionType.LESS_THAN
        || binary.getType() == BinaryExpressionType.LESS_THAN_OR_EQUAL
        || binary.getType() == BinaryExpressionType.GREATER_THAN
        || binary.getType() == BinaryExpressionType.GREATER_THAN_OR_EQUAL) {
      return Optional.empty(); // NOTE(rbd): I cannot think of any simplifications for these...
    } else if (binary.getType() == BinaryExpressionType.ADD) {
      if (binary.getLeft() instanceof LLConstantDeclaration addLeft) {
        if (addLeft.getValue() == 0) { // 0 + a => a
          return Optional.of(new LLCopy(binary.getRight(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration addRight) {
        if (addRight.getValue() == 0) { // a + 0 => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.SUBTRACT) {
      if (binary.getLeft() instanceof LLConstantDeclaration subLeft) {
        if (subLeft.getValue() == 0) { // 0 - a => -a
          return Optional.of(new LLUnary(UnaryExpressionType.NEGATE, binary.getRight(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration subRight) {
        if (subRight.getValue() == 0) { // a - 0 => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.MULTIPLY) {
      if (binary.getLeft() instanceof LLConstantDeclaration mulLeft) {
        if (mulLeft.getValue() == 1) { // 1 * a => a
          return Optional.of(new LLCopy(binary.getRight(), binary.getResult()));
        } else if (mulLeft.getValue() == 0) { // 0 * a => a
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else if (isPowerOf2(mulLeft.getValue())) { // 2^n * a => a << n
          final LLConstantDeclaration n = new LLConstantDeclaration(floorLog2(mulLeft.getValue()));
          return Optional.of(new LLBinary(binary.getRight(), BinaryExpressionType.SHIFT_LEFT, n, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration mulRight) {
        if (mulRight.getValue() == 1) { // a * 1 => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else if (mulRight.getValue() == 0) { // a * 0 => 0
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else if (isPowerOf2(mulRight.getValue())) { // a * 2^n => a << n
          final LLConstantDeclaration n = new LLConstantDeclaration(floorLog2(mulRight.getValue()));
          return Optional.of(new LLBinary(binary.getLeft(), BinaryExpressionType.SHIFT_LEFT, n, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.DIVIDE) {
      if (binary.getLeft() instanceof LLConstantDeclaration divLeft) {
        if (divLeft.getValue() == 0) { // 0 / a => 0
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration divRight) {
        if (divRight.getValue() == 1) { // a / 1 => a
          return Optional.of(new LLCopy(binary.getLeft(), binary.getResult()));
        } else if (divRight.getValue() == 0) { // a / 0 => DivideByZero
          return Optional.of(new LLException(LLException.Type.DivideByZero));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.MODULUS) {
      if (binary.getLeft() instanceof LLConstantDeclaration modLeft) {
        if (modLeft.getValue() == 0) { // 0 % a => 0
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else {
          return Optional.empty();
        }
      } else if (binary.getRight() instanceof LLConstantDeclaration modRight) {
        if (modRight.getValue() == 1) { // a % 1 => 0
          return Optional.of(new LLIntegerLiteral(0, binary.getResult()));
        } else if (modRight.getValue() == 0) { // a % 0 => DivideByZero
          return Optional.of(new LLException(LLException.Type.DivideByZero));
        } else {
          return Optional.empty();
        }
      } else {
        return Optional.empty();
      }
    } else if (binary.getType() == BinaryExpressionType.SHIFT_LEFT
        || binary.getType() == BinaryExpressionType.SHIFT_RIGHT) {
      return Optional.empty(); // NOTE(rbd): There are simplifications for these, but these are not exposed to the programmer...
    } else {
      throw new RuntimeException("unreachable");
    }
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
    } else if (binary.getType() == BinaryExpressionType.SHIFT_LEFT) {
      return new LLIntegerLiteral(leftConstant.getValue() << rightConstant.getValue(), binary.getResult());
    } else if (binary.getType() == BinaryExpressionType.SHIFT_RIGHT) {
      return new LLIntegerLiteral(leftConstant.getValue() >> rightConstant.getValue(), binary.getResult());
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

      if (instruction instanceof LLInternalCall internalCall) {
        // NOTE(rbd): Replace all global definitions with identity
        for (final LLDeclaration global : globals) {
          if (!identityIndices.containsKey(global)) {
            identityIndices.put(global, instructions.size());
            instructions.add(new LLCopy(global, global));
          }

          final int j = identityIndices.get(global);

          if (definitionIndices.containsKey(global)) {
            for (final int k : definitionIndices.get(global)) {
              current.clear(k);
            }
          }

          current.set(j);
          definitionIndices.put(global, new HashSet<>(Set.of(j)));
        }

        // NOTE(rbd): Replace all definitions using a global with identity
        for (final Map.Entry<LLDeclaration, Set<Integer>> definitionIndexEntry : definitionIndices.entrySet()) {
          final LLDeclaration definition = definitionIndexEntry.getKey();

          for (final int j : definitionIndexEntry.getValue()) {
            final LLInstruction definitionInstruction = instructions.get(j);
            boolean usesGlobal = false;

            for (final LLDeclaration use : definitionInstruction.uses()) {
              if (globals.contains(use)) {
                usesGlobal = true;
              }
            }

            if (usesGlobal) {
              if (!identityIndices.containsKey(definition)) {
                identityIndices.put(definition, instructions.size());
                instructions.add(new LLCopy(definition, definition));
              }

              final int k = identityIndices.get(definition);

              if (definitionIndices.containsKey(definition)) {
                for (final int l : definitionIndices.get(definition)) {
                  current.clear(l);
                }
              }

              current.set(k);
              definitionIndices.put(definition, new HashSet<>(Set.of(k)));
            }
          }
        }
      }

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (definitionIndices.containsKey(definition)) {
          for (final int j : definitionIndices.get(definition)) {
            current.clear(j);
          }
        }

        current.set(i);
        definitionIndices.put(definition, new HashSet<>(Set.of(i)));

        for (final Map.Entry<LLDeclaration, Set<Integer>> definitionIndexEntry : definitionIndices.entrySet()) {
          final LLDeclaration newDefinition = definitionIndexEntry.getKey();

          for (final int j : definitionIndexEntry.getValue()) {
            final LLInstruction definitionInstruction = instructions.get(j);
            boolean uses = false;

            for (final LLDeclaration use : definitionInstruction.uses()) {
              if (use == definition) {
                uses = true;
              }
            }

            if (uses) {
              if (!identityIndices.containsKey(newDefinition)) {
                identityIndices.put(newDefinition, instructions.size());
                instructions.add(new LLCopy(newDefinition, newDefinition));
              }

              final int k = identityIndices.get(newDefinition);

              if (definitionIndices.containsKey(newDefinition)) {
                for (final int l : definitionIndices.get(newDefinition)) {
                  current.clear(l);
                }
              }

              current.set(k);
              definitionIndices.put(newDefinition, new HashSet<>(Set.of(k)));
            }
          }
        }
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
        if (definitionIndices.containsKey(use) && definitionIndices.get(use).size() == 1) {
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
            final LLDeclaration newUse = definitionInstruction.uses().iterator().next();
            usesChanged = (use != newUse);
            newUses.add(newUse);
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
          } else if (algebraicSimplification) {
            final Optional<LLInstruction> simplified = trySimplifyBinary(binary);
            if (simplified.isPresent()) {
              instructionsChanged = true;
              newInstruction = simplified.get();
            }
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

      if (instruction instanceof LLInternalCall internalCall) {
        // NOTE(rbd): Replace all global definitions with identity
        for (final LLDeclaration global : globals) {
          if (!identityIndices.containsKey(global)) {
            identityIndices.put(global, instructions.size());
            instructions.add(new LLCopy(global, global));
          }

          final int j = identityIndices.get(global);

          definitionIndices.put(global, new HashSet<>(Set.of(j)));
        }

        // NOTE(rbd): Replace all definitions using a global with identity
        for (final Map.Entry<LLDeclaration, Set<Integer>> definitionIndexEntry : definitionIndices.entrySet()) {
          final LLDeclaration definition = definitionIndexEntry.getKey();

          for (final int j : definitionIndexEntry.getValue()) {
            final LLInstruction definitionInstruction = instructions.get(j);
            boolean usesGlobal = false;

            for (final LLDeclaration use : definitionInstruction.uses()) {
              if (globals.contains(use)) {
                usesGlobal = true;
              }
            }

            if (usesGlobal) {
              if (!identityIndices.containsKey(definition)) {
                identityIndices.put(definition, instructions.size());
                instructions.add(new LLCopy(definition, definition));
              }

              final int k = identityIndices.get(definition);

              definitionIndices.put(definition, new HashSet<>(Set.of(k)));
            }
          }
        }
      }

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        definitionIndices.put(definition, new HashSet<>(Set.of(i)));

        for (final Map.Entry<LLDeclaration, Set<Integer>> definitionIndexEntry : definitionIndices.entrySet()) {
          final LLDeclaration newDefinition = definitionIndexEntry.getKey();

          for (final int j : definitionIndexEntry.getValue()) {
            final LLInstruction definitionInstruction = instructions.get(j);
            boolean uses = false;

            for (final LLDeclaration use : definitionInstruction.uses()) {
              if (use == definition) {
                uses = true;
              }
            }

            if (uses) {
              if (!identityIndices.containsKey(newDefinition)) {
                identityIndices.put(newDefinition, instructions.size());
                instructions.add(new LLCopy(newDefinition, newDefinition));
              }

              final int k = identityIndices.get(newDefinition);

              definitionIndices.put(newDefinition, new HashSet<>(Set.of(k)));
            }
          }
        }
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
    identityIndices.clear();

    for (final LLDeclaration global : globals) {
      identityIndices.put(global, instructions.size());
      instructions.add(new LLCopy(global, global));
    }

    final Map<LLBasicBlock, Integer> indices = new HashMap<>();
    final Map<LLBasicBlock, BitSet> entries = new HashMap<>();
    final Map<LLBasicBlock, BitSet> exits = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new HashSet<>();

    final BitSet globalEntry = new BitSet();
    for (final LLDeclaration global : globals) {
      globalEntry.set(identityIndices.get(global));
    }

    indices.put(controlFlowGraph.getEntry(), instructions.size());
    entries.put(controlFlowGraph.getEntry(), globalEntry);
    exits.put(controlFlowGraph.getEntry(), new BitSet());

    instructions.addAll(controlFlowGraph.getEntry().getInstructions());

    workSet.addAll(controlFlowGraph.getEntry().getSuccessors());
    visited.add(controlFlowGraph.getEntry());

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
