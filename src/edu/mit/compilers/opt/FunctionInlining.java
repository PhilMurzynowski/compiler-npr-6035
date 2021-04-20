package edu.mit.compilers.opt;

import java.util.*;

import static java.util.Map.entry;

import edu.mit.compilers.hl.*;

public class FunctionInlining {

  private static void countUsages(final HLProgram program, final Map<HLMethodDeclaration, Integer> uses) {
    for (final HLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      uses.put(methodDeclaration, 0);
    }

    for (final HLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      final HLBlock block = methodDeclaration.getBody();
      for (final HLStatement statement : block.getStatements()) {
        if (statement instanceof HLCallStatement callStatement) {
          final HLCallExpression callExpression = callStatement.getCall();
          if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
            final HLMethodDeclaration declaration = internalCallExpression.getDeclaration();
            uses.put(declaration, uses.get(declaration) + 1);
          }
        }
      }
    }
  }

  // NOTE(rbd): If the cost is below this threshold, the function will be inlined. However, if a function is only called
  // once, it will always be inlined no matter the cost.
  private static final int COST_THRESHOLD = 20;

  // NOTE(rbd): This tries to yield costs (roughly) proportional to the number of instructions necessary to generate for
  // any given function. In other words, this is not the *time* complexity of the function, but the *space* complexity.
  private static final Map<Class<? extends HLNode>, Integer> COSTS = Map.ofEntries(
    entry(HLBinaryExpression.class,            1),
    entry(HLUnaryExpression.class,             1),
    entry(HLLoadScalarExpression.class,        1),
    entry(HLLoadArrayExpression.class,         1),
    entry(HLInternalCallExpression.class,      COST_THRESHOLD), // NOTE(rbd): Prevent inlining if there are nested internal method calls. (Think: Recursive method calls.)
    entry(HLExternalCallExpression.class,      1),
    entry(HLLengthExpression.class,            1),
    entry(HLIntegerLiteral.class,              1),
    entry(HLStringLiteral.class,               1),
    entry(HLStoreScalarStatement.class,        1),
    entry(HLStoreArrayStatement.class,         1),
    entry(HLStoreArrayCompoundStatement.class, 1),
    entry(HLCallStatement.class,               1),
    entry(HLIfStatement.class,                 1),
    entry(HLForStatement.class,                1),
    entry(HLWhileStatement.class,              1),
    entry(HLReturnStatement.class,             1),
    entry(HLBreakStatement.class,              1),
    entry(HLContinueStatement.class,           1),
    entry(HLBlock.class,                       1)
  );

  private static int estimateCost(final HLCallExpression callExpression) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      int cost = COSTS.get(HLInternalCallExpression.class);
      for (final HLArgument argument : internalCallExpression.getArguments()) {
        cost += estimateCost(argument);
      }
      return cost;
    } else if (callExpression instanceof HLExternalCallExpression externalCallExpression) {
      int cost = COSTS.get(HLExternalCallExpression.class);
      for (final HLArgument argument : externalCallExpression.getArguments()) {
        cost += estimateCost(argument);
      }
      return cost;
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static int estimateCost(final HLExpression expression) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      return COSTS.get(HLBinaryExpression.class) 
        + estimateCost(binaryExpression.getLeft()) 
        + estimateCost(binaryExpression.getRight());
    } else if (expression instanceof HLUnaryExpression unaryExpression) {
      return COSTS.get(HLUnaryExpression.class)
        + estimateCost(unaryExpression.getExpression());
    } else if (expression instanceof HLLoadExpression loadExpression) {
      if (loadExpression instanceof HLLoadScalarExpression loadScalarExpression) {
        return COSTS.get(HLLoadScalarExpression.class);
      } else if (loadExpression instanceof HLLoadArrayExpression loadArrayExpression) {
        return COSTS.get(HLLoadArrayExpression.class)
          + estimateCost(loadArrayExpression.getIndex());
      } else {
        throw new RuntimeException("unreachable");
      }
    } else if (expression instanceof HLCallExpression callExpression) {
      return estimateCost(callExpression);
    } else if (expression instanceof HLLengthExpression lengthExpression) {
      return COSTS.get(HLLengthExpression.class);
    } else if (expression instanceof HLIntegerLiteral integerLiteral) {
      return COSTS.get(HLIntegerLiteral.class);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static int estimateCost(final HLArgument argument) {
    if (argument instanceof HLStringLiteral stringLiteral) {
      return COSTS.get(HLStringLiteral.class);
    } else if (argument instanceof HLExpression expression) {
      return estimateCost(expression);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static int estimateCost(final HLStoreScalarStatement storeScalarStatement) {
    return COSTS.get(HLStoreScalarStatement.class)
      + estimateCost(storeScalarStatement.getExpression());
  }

  private static int estimateCost(final HLStoreStatement storeStatement) {
    if (storeStatement instanceof HLStoreScalarStatement storeScalarStatement) {
      return estimateCost(storeScalarStatement);
    } else if (storeStatement instanceof HLStoreArrayStatement storeArrayStatement) {
      return COSTS.get(HLStoreArrayStatement.class)
        + estimateCost(storeArrayStatement.getIndex())
        + estimateCost(storeArrayStatement.getExpression());
    } else if (storeStatement instanceof HLStoreArrayCompoundStatement storeArrayCompoundStatement) {
      int cost = COSTS.get(HLStoreArrayCompoundStatement.class)
        + estimateCost(storeArrayCompoundStatement.getIndex());
      if (storeArrayCompoundStatement.getExpression().isPresent()) {
        cost += estimateCost(storeArrayCompoundStatement.getExpression().get());
      };
      return cost;
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static int estimateCost(final HLStatement statement) {
    if (statement instanceof HLStoreStatement storeStatement) {
      return estimateCost(storeStatement);
    } else if (statement instanceof HLCallStatement callStatement) {
      return COSTS.get(HLCallStatement.class)
        + estimateCost(callStatement.getCall());
    } else if (statement instanceof HLIfStatement ifStatement) {
      int cost = COSTS.get(HLIfStatement.class)
        + estimateCost(ifStatement.getCondition())
        + estimateCost(ifStatement.getBody());
      if (ifStatement.getOther().isPresent()) {
        cost += estimateCost(ifStatement.getOther().get());
      }
      return cost;
    } else if (statement instanceof HLForStatement forStatement) {
      return COSTS.get(HLForStatement.class)
        + estimateCost(forStatement.getInitial())
        + estimateCost(forStatement.getCondition())
        + estimateCost(forStatement.getUpdate())
        + estimateCost(forStatement.getBody());
    } else if (statement instanceof HLWhileStatement whileStatement) {
      return COSTS.get(HLWhileStatement.class)
        + estimateCost(whileStatement.getCondition())
        + estimateCost(whileStatement.getBody());
    } else if (statement instanceof HLReturnStatement returnStatement) {
      int cost = COSTS.get(HLReturnStatement.class);
      if (returnStatement.getExpression().isPresent()) {
        cost += estimateCost(returnStatement.getExpression().get());
      }
      return cost;
    } else if (statement instanceof HLBreakStatement breakStatement) {
      return COSTS.get(HLBreakStatement.class);
    } else if (statement instanceof HLContinueStatement continueStatement) {
      return COSTS.get(HLContinueStatement.class);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static int estimateCost(final HLBlock block) {
    int cost = COSTS.get(HLBlock.class);
    for (final HLStatement statement : block.getStatements()) {
      cost += estimateCost(statement);
    }
    return cost;
  }

  private static void estimateCosts(final HLProgram program, final Map<HLMethodDeclaration, Integer> costs) {
    for (final HLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      costs.put(methodDeclaration, estimateCost(methodDeclaration.getBody()));
    }
  }

  private static void apply(final HLCallExpression callExpression, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      for (final HLArgument argument : internalCallExpression.getArguments()) {
        apply(argument, uses, costs);
      }

      final HLMethodDeclaration methodDeclaration = internalCallExpression.getDeclaration();

      if (uses.get(methodDeclaration) == 1 || costs.get(methodDeclaration) < COST_THRESHOLD) {
        internalCallExpression.setInline();
      }
    } else if (callExpression instanceof HLExternalCallExpression externalCallExpression) {
      for (final HLArgument argument : externalCallExpression.getArguments()) {
        apply(argument, uses, costs);
      }
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static void apply(final HLExpression expression, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      apply(binaryExpression.getLeft(), uses, costs);
      apply(binaryExpression.getRight(), uses, costs);
    } else if (expression instanceof HLUnaryExpression unaryExpression) {
      apply(unaryExpression.getExpression(), uses, costs);
    } else if (expression instanceof HLLoadExpression loadExpression) {
      if (loadExpression instanceof HLLoadScalarExpression loadScalarExpression) {
        // NOTE(rbd): Nothing to apply.
      } else if (loadExpression instanceof HLLoadArrayExpression loadArrayExpression) {
        apply(loadArrayExpression.getIndex(), uses, costs);
      } else {
        throw new RuntimeException("unreachable");
      }
    } else if (expression instanceof HLCallExpression callExpression) {
      apply(callExpression, uses, costs);
    } else if (expression instanceof HLLengthExpression lengthExpression) {
      // NOTE(rbd): Nothing to apply.
    } else if (expression instanceof HLIntegerLiteral integerLiteral) {
      // NOTE(rbd): Nothing to apply.
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static void apply(final HLArgument argument, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    if (argument instanceof HLStringLiteral stringLiteral) {
      // NOTE(rbd): Nothing to apply.
    } else if (argument instanceof HLExpression expression) {
      apply(expression, uses, costs);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static void apply(final HLStoreScalarStatement storeScalarStatement, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    apply(storeScalarStatement.getExpression(), uses, costs);
  }

  private static void apply(final HLStoreStatement storeStatement, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    if (storeStatement instanceof HLStoreScalarStatement storeScalarStatement) {
      apply(storeScalarStatement, uses, costs);
    } else if (storeStatement instanceof HLStoreArrayStatement storeArrayStatement) {
      apply(storeArrayStatement.getIndex(), uses, costs);
      apply(storeArrayStatement.getExpression(), uses, costs);
    } else if (storeStatement instanceof HLStoreArrayCompoundStatement storeArrayCompoundStatement) {
      apply(storeArrayCompoundStatement.getIndex(), uses, costs);
      if (storeArrayCompoundStatement.getExpression().isPresent()) {
        apply(storeArrayCompoundStatement.getExpression().get(), uses, costs);
      }
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static void apply(final HLStatement statement, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    if (statement instanceof HLStoreStatement storeStatement) {
      apply(storeStatement, uses, costs);
    } else if (statement instanceof HLCallStatement callStatement) {
      apply(callStatement.getCall(), uses, costs);
    } else if (statement instanceof HLIfStatement ifStatement) {
      apply(ifStatement.getCondition(), uses, costs);
      apply(ifStatement.getBody(), uses, costs);
      if (ifStatement.getOther().isPresent()) {
        apply(ifStatement.getOther().get(), uses, costs);
      }
    } else if (statement instanceof HLForStatement forStatement) {
      apply(forStatement.getInitial(), uses, costs);
      apply(forStatement.getCondition(), uses, costs);
      apply(forStatement.getUpdate(), uses, costs);
      apply(forStatement.getBody(), uses, costs);
    } else if (statement instanceof HLWhileStatement whileStatement) {
      apply(whileStatement.getCondition(), uses, costs);
      apply(whileStatement.getBody(), uses, costs);
    } else if (statement instanceof HLReturnStatement returnStatement) {
      if (returnStatement.getExpression().isPresent()) {
        apply(returnStatement.getExpression().get(), uses, costs);
      }
    } else if (statement instanceof HLBreakStatement breakStatement) {
      // NOTE(rbd): Nothing to apply.
    } else if (statement instanceof HLContinueStatement continueStatement) {
      // NOTE(rbd): Nothing to apply.
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  private static void apply(final HLBlock block, final Map<HLMethodDeclaration, Integer> uses, final Map<HLMethodDeclaration, Integer> costs) {
    for (final HLStatement statement : block.getStatements()) {
      apply(statement, uses, costs);
    }
  }

  public static void apply(final HLProgram program) {
    final Map<HLMethodDeclaration, Integer> uses = new HashMap<>();
    countUsages(program, uses);

    final Map<HLMethodDeclaration, Integer> costs = new HashMap<>();
    estimateCosts(program, costs);

    for (final HLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      // NOTE(rbd): Do not generate a function if it is never called. Also, if a function is only called once, it is
      // always inlined.
      if (uses.get(methodDeclaration) > 1 || methodDeclaration.getIdentifier().equals("main")) {
        apply(methodDeclaration.getBody(), uses, costs);
      }
    }
  }

}
