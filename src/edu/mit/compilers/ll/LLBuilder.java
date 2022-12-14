package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import edu.mit.compilers.hl.*;
import edu.mit.compilers.common.*;

public class LLBuilder {

  public static LLProgram buildProgram(HLProgram program) {
    final LLProgram.Builder builder = new LLProgram.Builder();

    for (HLImportDeclaration hlImportDeclaration : program.getImportDeclarations()) {
      final LLImportDeclaration llImportDeclaration = LLBuilder.buildImportDeclaration(hlImportDeclaration);
      builder.addImport(llImportDeclaration);
    }
    for (HLGlobalScalarFieldDeclaration hlGlobalScalarFieldDeclaration : program.getGlobalScalarFieldDeclarations()) {
      final LLGlobalScalarFieldDeclaration llGlobalScalarFieldDeclaration = LLBuilder.buildGlobalScalarFieldDeclaration(hlGlobalScalarFieldDeclaration);
      builder.addScalar(llGlobalScalarFieldDeclaration);
    }
    for (HLGlobalArrayFieldDeclaration hlGlobalArrayFieldDeclaration : program.getGlobalArrayFieldDeclarations()) {
      final LLGlobalArrayFieldDeclaration llGlobalArrayFieldDeclaration = LLBuilder.buildGlobalArrayFieldDeclaration(hlGlobalArrayFieldDeclaration);
      builder.addArray(llGlobalArrayFieldDeclaration);
    }
    for (HLStringLiteralDeclaration hlStringLiteralDeclaration : program.getStringLiteralDeclarations()) {
      final LLStringLiteralDeclaration llStringLiteralDeclaration = LLBuilder.buildStringLiteralDeclaration(hlStringLiteralDeclaration);
      builder.addString(llStringLiteralDeclaration);
    }

    for (HLMethodDeclaration hlMethodDeclaration : program.getMethodDeclarations()) {
      final LLMethodDeclaration llMethodDeclaration = LLBuilder.buildMethodDeclaration(hlMethodDeclaration);

      builder.addMethod(llMethodDeclaration);
    }

    return builder.build();
  }

  // DONE: Phil
  public static LLImportDeclaration buildImportDeclaration(HLImportDeclaration importDeclaration) {
    final LLImportDeclaration llImportDeclaration = new LLImportDeclaration(importDeclaration.getIdentifer());
    importDeclaration.setLL(llImportDeclaration);
    return llImportDeclaration;
  }

  // DONE: Noah
  public static LLScalarFieldDeclaration buildScalarFieldDeclaration(HLScalarFieldDeclaration scalarFieldDeclaration) {
    throw new RuntimeException("should never be called");
  }

  public static LLGlobalScalarFieldDeclaration buildGlobalScalarFieldDeclaration(HLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    final LLGlobalScalarFieldDeclaration llGlobalScalarFieldDeclaration = new LLGlobalScalarFieldDeclaration(globalScalarFieldDeclaration.getIdentifier());
    globalScalarFieldDeclaration.setLL(llGlobalScalarFieldDeclaration);
    return llGlobalScalarFieldDeclaration;
  }

  // DONE: Phil
  public static LLGlobalArrayFieldDeclaration buildGlobalArrayFieldDeclaration(HLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    final LLGlobalArrayFieldDeclaration llGlobalArrayFieldDeclaration = new LLGlobalArrayFieldDeclaration(globalArrayFieldDeclaration.getIdentifier(), globalArrayFieldDeclaration.getLength().getValue());
    globalArrayFieldDeclaration.setLL(llGlobalArrayFieldDeclaration);
    return llGlobalArrayFieldDeclaration;
  }

  // DONE: Noah
  public static LLStringLiteralDeclaration buildStringLiteralDeclaration(HLStringLiteralDeclaration stringLiteralDeclaration) {
    final LLStringLiteralDeclaration declaration = new LLStringLiteralDeclaration(
        stringLiteralDeclaration.getIndex(),
        stringLiteralDeclaration.getValue()
    );
    stringLiteralDeclaration.setLL(declaration);
    return declaration;
  }

  public static LLMethodDeclaration buildMethodDeclaration(HLMethodDeclaration hlMethodDeclaration) {
    final LLMethodDeclaration llMethodDeclaration = new LLMethodDeclaration(hlMethodDeclaration.getIdentifier(), hlMethodDeclaration.getMethodType());
    hlMethodDeclaration.setLL(llMethodDeclaration);

    // WARN(rbd): The lines above MUST be executed before the lines below. (Think: Recursive method calls.)

    LLControlFlowGraph bodyCFG = LLBuilder.buildBlock(hlMethodDeclaration.getBody(), llMethodDeclaration, Optional.empty(), Optional.empty(), Map.of(), Optional.empty(), Optional.empty());

    if (llMethodDeclaration.getMethodType() == MethodType.VOID) {
      bodyCFG = bodyCFG.concatenate(
        new LLReturn(Optional.empty(), llMethodDeclaration)
      );
    } else {
      bodyCFG = bodyCFG.concatenate(
        new LLException(LLException.Type.NoReturnValue)
      );
    }
    if (llMethodDeclaration.hasOutOfBoundsExceptionBB()) {
      bodyCFG.addException(llMethodDeclaration.getOutOfBoundsExceptionBB());
    }

    // NOTE(rbd): You can remove `.simplify()` here if you think simplification is the problem. :)
    bodyCFG.simplify();
    llMethodDeclaration.setBody(bodyCFG);

    return llMethodDeclaration;
  }

  // DONE: Robert
  public static LLArgumentDeclaration buildArgumentDeclaration(HLArgumentDeclaration argumentDeclaration, LLMethodDeclaration methodDeclaration) {
    final LLArgumentDeclaration llArgumentDeclaration = new LLArgumentDeclaration(argumentDeclaration.getIndex());
    argumentDeclaration.setLL(llArgumentDeclaration);
    return llArgumentDeclaration;
  }

  // DONE: Phil
  public static LLLocalScalarFieldDeclaration buildLocalScalarFieldDeclaration(HLLocalScalarFieldDeclaration localScalarFieldDeclaration, LLMethodDeclaration methodDeclaration) {
    final LLLocalScalarFieldDeclaration declaration = new LLLocalScalarFieldDeclaration(methodDeclaration.scalarIndex());
    localScalarFieldDeclaration.setLL(declaration);
    return declaration;
  }

  // DONE: Noah
  public static LLLocalArrayFieldDeclaration buildLocalArrayFieldDeclaration(HLLocalArrayFieldDeclaration localArrayFieldDeclaration, LLMethodDeclaration methodDeclaration) {
    final LLLocalArrayFieldDeclaration declaration = new LLLocalArrayFieldDeclaration(
        methodDeclaration.arrayIndex(),
        localArrayFieldDeclaration.getLength().getValue()
    );
    localArrayFieldDeclaration.setLL(declaration);
    return declaration;
  }

  // DONE: Robert
  public static LLControlFlowGraph buildBlock(HLBlock block, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    if (argumentAliases.isEmpty()) {
      for (HLArgumentDeclaration hlArgumentDeclaration : block.getArgumentDeclarations()) {
        final LLArgumentDeclaration llArgumentDeclaration = LLBuilder.buildArgumentDeclaration(hlArgumentDeclaration, methodDeclaration);

        methodDeclaration.addArgument(llArgumentDeclaration);
      }
    }

    for (HLLocalScalarFieldDeclaration hlLocalScalarFieldDeclaration : block.getScalarFieldDeclarations()) {
      final LLLocalScalarFieldDeclaration llLocalScalarFieldDeclaration = LLBuilder.buildLocalScalarFieldDeclaration(hlLocalScalarFieldDeclaration, methodDeclaration);

      methodDeclaration.addScalar(llLocalScalarFieldDeclaration);

      resultCFG = resultCFG.concatenate(
        new LLStoreScalar(hlLocalScalarFieldDeclaration.getLL(), new LLConstantDeclaration(0))
      );
    }

    for (HLLocalArrayFieldDeclaration hlLocalArrayFieldDeclaration : block.getArrayFieldDeclarations()) {
      final LLLocalArrayFieldDeclaration llLocalArrayFieldDeclaration = LLBuilder.buildLocalArrayFieldDeclaration(hlLocalArrayFieldDeclaration, methodDeclaration);

      methodDeclaration.addArray(llLocalArrayFieldDeclaration);

      final LLAliasDeclaration indexResult = methodDeclaration.newAlias();

      final LLBasicBlock initialBB = new LLBasicBlock(
        new LLIntegerLiteral(0, indexResult)
      );

      final LLBasicBlock conditionBB = new LLBasicBlock(
        new LLCompare(indexResult, ComparisonType.LESS_THAN, new LLConstantDeclaration(llLocalArrayFieldDeclaration.getLength()))
      );

      final LLBasicBlock bodyBB = new LLBasicBlock(
        new LLStoreArray(llLocalArrayFieldDeclaration, indexResult, new LLConstantDeclaration(0))
      );

      final LLBasicBlock updateBB = new LLBasicBlock(
        new LLUnary(UnaryExpressionType.INCREMENT, indexResult, indexResult)
      );

      final LLBasicBlock exitBB = new LLBasicBlock();

      LLBasicBlock.setTrueTarget(initialBB, conditionBB);

      LLBasicBlock.setTrueTarget(conditionBB, bodyBB);
      LLBasicBlock.setFalseTarget(conditionBB, exitBB);

      LLBasicBlock.setTrueTarget(bodyBB, updateBB);

      LLBasicBlock.setTrueTarget(updateBB, conditionBB);

      resultCFG = resultCFG.concatenate(
        new LLControlFlowGraph(initialBB, exitBB)
      );
    }

    for (HLStatement statement : block.getStatements()) {
      resultCFG = resultCFG.concatenate(LLBuilder.buildStatement(statement, methodDeclaration, breakTarget, continueTarget, argumentAliases, returnResult, returnTarget));

      if (statement instanceof HLReturnStatement || statement instanceof HLBreakStatement || statement instanceof HLContinueStatement) {
        break;
      }
    }

    return resultCFG;
  }

  public static LLControlFlowGraph buildStatement(HLStatement statement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    if (statement instanceof HLStoreStatement storeStatement) {
      return LLBuilder.buildStoreStatement(storeStatement, methodDeclaration, argumentAliases);
    } else if (statement instanceof HLCallStatement callStatement) {
      return LLBuilder.buildCallStatement(callStatement, methodDeclaration, argumentAliases);
    } else if (statement instanceof HLIfStatement ifStatement) {
      return LLBuilder.buildIfStatement(ifStatement, methodDeclaration, breakTarget, continueTarget, argumentAliases, returnResult, returnTarget);
    } else if (statement instanceof HLForStatement forStatement) {
      return LLBuilder.buildForStatement(forStatement, methodDeclaration, argumentAliases, returnResult, returnTarget);
    } else if (statement instanceof HLWhileStatement whileStatement) {
      return LLBuilder.buildWhileStatement(whileStatement, methodDeclaration, argumentAliases, returnResult, returnTarget);
    } else if (statement instanceof HLReturnStatement returnStatement) {
      return LLBuilder.buildReturnStatement(returnStatement, methodDeclaration, argumentAliases, returnResult, returnTarget);
    } else if (statement instanceof HLBreakStatement breakStatement) {
      return LLBuilder.buildBreakStatement(breakStatement, methodDeclaration, breakTarget, continueTarget);
    } else if (statement instanceof  HLContinueStatement continueStatement) {
      return LLBuilder.buildContinueStatement(continueStatement, methodDeclaration, breakTarget, continueTarget);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildStoreStatement(HLStoreStatement storeStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (storeStatement instanceof HLStoreScalarStatement storeScalarStatement) {
      return LLBuilder.buildStoreScalarStatement(storeScalarStatement, methodDeclaration, argumentAliases);
    } else if (storeStatement instanceof HLStoreArrayStatement storeArrayStatement) {
      return LLBuilder.buildStoreArrayStatement(storeArrayStatement, methodDeclaration, argumentAliases);
    } else if (storeStatement instanceof HLStoreArrayCompoundStatement storeArrayCompoundStatement) {
      return LLBuilder.buildStoreArrayCompoundStatement(storeArrayCompoundStatement, methodDeclaration, argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildStoreScalarStatement(HLStoreScalarStatement storeScalarStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration expressionResult = methodDeclaration.newAlias();
    final LLControlFlowGraph expressionCFG = LLBuilder.buildExpression(storeScalarStatement.getExpression(), methodDeclaration, expressionResult, argumentAliases);
    resultCFG = resultCFG.concatenate(expressionCFG);

    if (argumentAliases.containsKey(storeScalarStatement.getDeclaration())) {
      resultCFG = resultCFG.concatenate(
        new LLCopy(expressionResult, argumentAliases.get(storeScalarStatement.getDeclaration()))
      );
    } else {
      resultCFG = resultCFG.concatenate(
        new LLStoreScalar(storeScalarStatement.getDeclaration().getLL(), expressionResult)
      );
    }

    return resultCFG;
  }

  // DONE: Noah
  public static LLControlFlowGraph buildStoreArrayStatement(HLStoreArrayStatement storeArrayStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    // index expression evaluation
    final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
    final LLControlFlowGraph indexCFG = LLBuilder.buildExpression(storeArrayStatement.getIndex(), methodDeclaration, indexResult, argumentAliases);
    resultCFG = resultCFG.concatenate(indexCFG);

    // value expression evaluation
    final LLAliasDeclaration valueResult = methodDeclaration.newAlias();
    final LLControlFlowGraph valueCFG = LLBuilder.buildExpression(storeArrayStatement.getExpression(), methodDeclaration, valueResult, argumentAliases);
    resultCFG = resultCFG.concatenate(valueCFG);

    final LLBasicBlock storeBB = new LLBasicBlock(
      new LLStoreArray(storeArrayStatement.getDeclaration().getLL(), indexResult, valueResult)
    );

    final LLControlFlowGraph boundsCheckCFG = buildBoundsCheck(
      methodDeclaration,
      storeArrayStatement.getDeclaration().getLL().getLength(),
      indexResult,
      storeBB
    );

    resultCFG = resultCFG.concatenate(boundsCheckCFG);

    return new LLControlFlowGraph(resultCFG.getEntry(), storeBB);
  }

  public static LLControlFlowGraph buildStoreArrayCompoundStatement(HLStoreArrayCompoundStatement storeArrayCompoundStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    // index expression evaluation
    final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
    final LLControlFlowGraph indexCFG = LLBuilder.buildExpression(storeArrayCompoundStatement.getIndex(), methodDeclaration, indexResult, argumentAliases);
    resultCFG = resultCFG.concatenate(indexCFG);

    final LLAliasDeclaration loadResult = methodDeclaration.newAlias();
    final LLBasicBlock loadBB = new LLBasicBlock(
      new LLLoadArray(storeArrayCompoundStatement.getDeclaration().getLL(), indexResult, loadResult)
    );

    final LLControlFlowGraph boundsCheckCFG = buildBoundsCheck(
      methodDeclaration,
      storeArrayCompoundStatement.getDeclaration().getLL().getLength(),
      indexResult,
      loadBB
    );

    LLBasicBlock.setTrueTarget(resultCFG.expectExit(), boundsCheckCFG.getEntry());

    resultCFG = new LLControlFlowGraph(resultCFG.getEntry(), loadBB);

    if (storeArrayCompoundStatement.getExpression().isPresent()) {

      final LLAliasDeclaration expressionResult = methodDeclaration.newAlias();
      final LLControlFlowGraph expressionCFG = LLBuilder.buildExpression(storeArrayCompoundStatement.getExpression().get(), methodDeclaration, expressionResult, argumentAliases);
      resultCFG = resultCFG.concatenate(expressionCFG);

      final LLAliasDeclaration valueResult = methodDeclaration.newAlias();
      resultCFG = resultCFG.concatenate(
        new LLBinary(loadResult, storeArrayCompoundStatement.getType().toBinaryExpressionType(), expressionResult, valueResult),
        new LLStoreArray(storeArrayCompoundStatement.getDeclaration().getLL(), indexResult, valueResult)
      );

    } else {
      
      final LLAliasDeclaration valueResult = methodDeclaration.newAlias();
      resultCFG = resultCFG.concatenate(
        new LLUnary(storeArrayCompoundStatement.getType().toUnaryExpressionType(), loadResult, valueResult),
        new LLStoreArray(storeArrayCompoundStatement.getDeclaration().getLL(), indexResult, valueResult)
      );

    }

    return resultCFG;

  }

  // DONE: Phil
  public static LLControlFlowGraph buildCallStatement(HLCallStatement callStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    final HLCallExpression callExpression = callStatement.getCall();

    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      if (internalCallExpression.getDeclaration().getMethodType() == MethodType.VOID) {
        return LLBuilder.buildInternalCallExpression(internalCallExpression, methodDeclaration, Optional.empty(), argumentAliases);
      } else {
        final LLAliasDeclaration callResult = methodDeclaration.newAlias();
        return LLBuilder.buildInternalCallExpression(internalCallExpression, methodDeclaration, Optional.of(callResult), argumentAliases);
      }
    } else if (callExpression instanceof HLExternalCallExpression externalCallExpression) {
      final LLAliasDeclaration callResult = methodDeclaration.newAlias();
      return LLBuilder.buildExternalCallExpression(externalCallExpression, methodDeclaration, callResult, argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Robert
  public static LLControlFlowGraph buildIfStatement(HLIfStatement ifStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    final LLControlFlowGraph bodyCFG = LLBuilder.buildBlock(ifStatement.getBody(), methodDeclaration, breakTarget, continueTarget, argumentAliases, returnResult, returnTarget);
    final LLControlFlowGraph otherCFG;
    if (ifStatement.getOther().isPresent()) {
      otherCFG = LLBuilder.buildBlock(ifStatement.getOther().get(), methodDeclaration, breakTarget, continueTarget, argumentAliases, returnResult, returnTarget);
    } else {
      otherCFG = LLControlFlowGraph.empty();
    }
    final LLBasicBlock exitBB = new LLBasicBlock();
    final LLBasicBlock entryBB = LLShortCircuit.shortExpression(ifStatement.getCondition(), methodDeclaration, bodyCFG.getEntry(), otherCFG.getEntry(), argumentAliases);

    if (breakTarget.isPresent()) {
      if (bodyCFG.expectExit() != breakTarget.get() && bodyCFG.expectExit() != continueTarget.get() && (returnTarget.isEmpty() || bodyCFG.expectExit() != returnTarget.get())) {
        LLBasicBlock.setTrueTarget(bodyCFG.expectExit(), exitBB);
      }
      if (otherCFG.expectExit() != breakTarget.get() && otherCFG.expectExit() != continueTarget.get() && (returnTarget.isEmpty() || otherCFG.expectExit() != returnTarget.get())) {
        LLBasicBlock.setTrueTarget(otherCFG.expectExit(), exitBB);
      }
    } else {
      if (returnTarget.isEmpty() || bodyCFG.expectExit() != returnTarget.get()) {
        LLBasicBlock.setTrueTarget(bodyCFG.expectExit(), exitBB);
      }
      if (returnTarget.isEmpty() || otherCFG.expectExit() != returnTarget.get()) {
        LLBasicBlock.setTrueTarget(otherCFG.expectExit(), exitBB);
      }
    }

    return new LLControlFlowGraph(entryBB, exitBB);
  }

  // DONE: Noah
  public static LLControlFlowGraph buildForStatement(HLForStatement forStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    final LLBasicBlock breakBB = new LLBasicBlock();
    final LLControlFlowGraph updateCFG = buildStoreStatement(forStatement.getUpdate(), methodDeclaration, argumentAliases);
    final LLControlFlowGraph initialCFG = buildStoreScalarStatement(forStatement.getInitial(), methodDeclaration, argumentAliases);
    final LLControlFlowGraph bodyCFG = buildBlock(
      forStatement.getBody(),
      methodDeclaration,
      Optional.of(breakBB),
      Optional.of(updateCFG.getEntry()),
      argumentAliases,
      returnResult,
      returnTarget
    );
    final LLBasicBlock conditionBB = LLShortCircuit.shortExpression(
      forStatement.getCondition(), methodDeclaration, bodyCFG.getEntry(), breakBB, argumentAliases
    );

    LLBasicBlock.setTrueTarget(initialCFG.expectExit(), conditionBB);

    if (bodyCFG.expectExit() != updateCFG.getEntry() && bodyCFG.expectExit() != breakBB && (returnTarget.isEmpty() || bodyCFG.expectExit() != returnTarget.get())) {
      LLBasicBlock.setTrueTarget(bodyCFG.expectExit(), updateCFG.getEntry());
    }
    LLBasicBlock.setTrueTarget(updateCFG.expectExit(), conditionBB);

    return new LLControlFlowGraph(initialCFG.getEntry(), breakBB);
  }

  // DONE: Phil
  public static LLControlFlowGraph buildWhileStatement(HLWhileStatement whileStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    final LLBasicBlock breakBB = new LLBasicBlock();
    final LLBasicBlock continueBB = new LLBasicBlock();
    final LLControlFlowGraph bodyCFG = buildBlock(whileStatement.getBody(), methodDeclaration, Optional.of(breakBB), Optional.of(continueBB), argumentAliases, returnResult, returnTarget);
    final LLBasicBlock conditionBB = LLShortCircuit.shortExpression(whileStatement.getCondition(), methodDeclaration, bodyCFG.getEntry(), breakBB, argumentAliases);

    if (bodyCFG.expectExit() != breakBB && bodyCFG.expectExit() != continueBB && (returnTarget.isEmpty() || bodyCFG.expectExit() != returnTarget.get())) {
      LLBasicBlock.setTrueTarget(bodyCFG.expectExit(), continueBB);
    }

    LLBasicBlock.setTrueTarget(continueBB, conditionBB);

    return new LLControlFlowGraph(conditionBB, breakBB);
  }

  public static LLControlFlowGraph buildReturnStatement(HLReturnStatement returnStatement, LLMethodDeclaration methodDeclaration, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases, Optional<LLDeclaration> returnResult, Optional<LLBasicBlock> returnTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    Optional<HLExpression> hlExpression = returnStatement.getExpression();
    if (hlExpression.isPresent()) {
      final LLAliasDeclaration expressionResult = methodDeclaration.newAlias();
      final LLControlFlowGraph expressionCFG = LLBuilder.buildExpression(hlExpression.get(), methodDeclaration, expressionResult, argumentAliases);
      resultCFG = resultCFG.concatenate(expressionCFG);
      if (returnResult.isPresent()) {
        resultCFG = resultCFG.concatenate(
          new LLCopy(expressionResult, returnResult.get())
        );
        resultCFG = resultCFG.concatenate(returnTarget.get());
      } else {
        resultCFG = resultCFG.concatenate(
          new LLReturn(Optional.of(expressionResult), methodDeclaration)
        );
      }
    } else {
      if (returnTarget.isPresent()) { // function is inlined
        resultCFG = resultCFG.concatenate(returnTarget.get());
      } else {
        resultCFG = resultCFG.concatenate(
            new LLReturn(Optional.empty(), methodDeclaration)
        );
      }
    }

    return resultCFG;
  }

  // DONE: Robert
  public static LLControlFlowGraph buildBreakStatement(HLBreakStatement breakStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    if (breakTarget.isEmpty()) {
      throw new RuntimeException("break target does not exist");
    }

    resultCFG = resultCFG.concatenate(breakTarget.get());

    return resultCFG;
  }

  // DONE: Noah
  public static LLControlFlowGraph buildContinueStatement(HLContinueStatement continueStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    if (continueTarget.isEmpty()) {
      throw new RuntimeException("continue target does not exist");
    }

    resultCFG = resultCFG.concatenate(continueTarget.get());

    return resultCFG;
  }

  // DONE: Phil
  public static LLControlFlowGraph buildArgument(HLArgument argument, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (argument instanceof HLStringLiteral stringLiteral) {
      return LLBuilder.buildStringLiteral(stringLiteral, methodDeclaration, result);
    } else if (argument instanceof HLExpression expression) {
      return LLBuilder.buildExpression(expression, methodDeclaration, result, argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      return LLBuilder.buildBinaryExpression(binaryExpression, methodDeclaration, result, argumentAliases);
    } else if (expression instanceof HLUnaryExpression unaryExpression) {
      return LLBuilder.buildUnaryExpression(unaryExpression, methodDeclaration, result, argumentAliases);
    } else if (expression instanceof HLLoadExpression loadExpression) {
      return LLBuilder.buildLoadExpression(loadExpression, methodDeclaration, result, argumentAliases);
    } else if (expression instanceof HLCallExpression callExpression) {
      return LLBuilder.buildCallExpression(callExpression, methodDeclaration, Optional.of(result), argumentAliases);
    } else if (expression instanceof HLLengthExpression lengthExpression) {
      return LLBuilder.buildLengthExpression(lengthExpression, methodDeclaration, result);
    } else if (expression instanceof HLIntegerLiteral integerLiteral) {
      return LLBuilder.buildIntegerLiteral(integerLiteral, methodDeclaration, result);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildBinaryExpression(HLBinaryExpression binaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (binaryExpression.getType() == BinaryExpressionType.AND || binaryExpression.getType() == BinaryExpressionType.OR) {
      LLBasicBlock trueBB = new LLBasicBlock(
        new LLIntegerLiteral(1, result)
      );
      LLBasicBlock falseBB = new LLBasicBlock(
        new LLIntegerLiteral(0, result)
      );
      LLBasicBlock entryBB = LLShortCircuit.shortExpression(binaryExpression, methodDeclaration, trueBB, falseBB, argumentAliases);
      LLBasicBlock exitBB = new LLBasicBlock();
      
      LLBasicBlock.setTrueTarget(trueBB, exitBB);
      LLBasicBlock.setTrueTarget(falseBB, exitBB);

      return new LLControlFlowGraph(entryBB, exitBB);
    } else {
      LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

      final LLAliasDeclaration leftResult = methodDeclaration.newAlias();
      final LLControlFlowGraph leftCFG = LLBuilder.buildExpression(binaryExpression.getLeft(), methodDeclaration, leftResult, argumentAliases);
      resultCFG = resultCFG.concatenate(leftCFG);

      final LLAliasDeclaration rightResult = methodDeclaration.newAlias();
      final LLControlFlowGraph rightCFG = LLBuilder.buildExpression(binaryExpression.getRight(), methodDeclaration, rightResult, argumentAliases);
      resultCFG = resultCFG.concatenate(rightCFG);

      resultCFG = resultCFG.concatenate(
        new LLBinary(leftResult, binaryExpression.getType(), rightResult, result)
      );

      return resultCFG;
    }
  }

  public static LLControlFlowGraph buildUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration unaryResult = methodDeclaration.newAlias();
    final LLControlFlowGraph unaryCFG = LLBuilder.buildExpression(unaryExpression.getExpression(), methodDeclaration, unaryResult, argumentAliases);
    resultCFG = resultCFG.concatenate(unaryCFG);

    resultCFG = resultCFG.concatenate(
      new LLUnary(unaryExpression.getType(), unaryResult, result)
    );

    return resultCFG;
  }

  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (loadExpression instanceof HLLoadScalarExpression loadScalarExpression) {
      return LLBuilder.buildLoadScalarExpression(loadScalarExpression, result, argumentAliases);
    } else if (loadExpression instanceof HLLoadArrayExpression loadArrayExpression) {
      return LLBuilder.buildLoadArrayExpression(loadArrayExpression, methodDeclaration, result, argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    if (argumentAliases.containsKey(loadScalarExpression.getDeclaration())) {
      resultCFG = resultCFG.concatenate(
        new LLCopy(argumentAliases.get(loadScalarExpression.getDeclaration()), result)
      );
    } else {
      resultCFG = resultCFG.concatenate(
        new LLLoadScalar(loadScalarExpression.getDeclaration().getLL(), result)
      );
    }

    return resultCFG;
  }

  private static LLControlFlowGraph buildBoundsCheck(LLMethodDeclaration methodDeclaration, long length, LLDeclaration indexResult, LLBasicBlock targetBB) {
    final LLBasicBlock initialBB = new LLBasicBlock();

    final LLBasicBlock isPositiveBB = new LLBasicBlock(
      new LLCompare(indexResult, ComparisonType.GREATER_THAN_OR_EQUAL, new LLConstantDeclaration(0))
    );

    final LLBasicBlock isLessBB = new LLBasicBlock(
      new LLCompare(indexResult, ComparisonType.LESS_THAN, new LLConstantDeclaration(length))
    );

    LLBasicBlock.setTrueTarget(initialBB, isPositiveBB);

    LLBasicBlock.setTrueTarget(isPositiveBB, isLessBB);
    LLBasicBlock.setFalseTarget(isPositiveBB, methodDeclaration.getOutOfBoundsExceptionBB());

    LLBasicBlock.setTrueTarget(isLessBB, targetBB);
    LLBasicBlock.setFalseTarget(isLessBB, methodDeclaration.getOutOfBoundsExceptionBB());

    // NOTE(rbd): just to satisfy the single exit property of CFGs (will never be visited).
    // LLBasicBlock.setTrueTarget(exceptionBB, targetBB);

    return new LLControlFlowGraph(initialBB, targetBB);
  }

  // DONE: Robert
  public static LLControlFlowGraph buildLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
    final LLControlFlowGraph indexCFG = LLBuilder.buildExpression(loadArrayExpression.getIndex(), methodDeclaration, indexResult, argumentAliases);
    resultCFG = resultCFG.concatenate(indexCFG);

    final LLBasicBlock loadBB = new LLBasicBlock(
      new LLLoadArray(loadArrayExpression.getDeclaration().getLL(), indexResult, result)
    );

    final LLControlFlowGraph boundsCheckCFG = buildBoundsCheck(
      methodDeclaration, 
      loadArrayExpression.getDeclaration().getLL().getLength(),
      indexResult,
      loadBB
    );

    resultCFG = resultCFG.concatenate(boundsCheckCFG);

    return new LLControlFlowGraph(indexCFG.getEntry(), loadBB);
  }

  // DONE: Noah
  public static LLControlFlowGraph buildCallExpression(HLCallExpression callExpression, LLMethodDeclaration methodDeclaration, Optional<LLDeclaration> result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      return buildInternalCallExpression(internalCallExpression, methodDeclaration, result, argumentAliases);
    } else if (callExpression instanceof HLExternalCallExpression externalCallExpression) {
      return buildExternalCallExpression(externalCallExpression, methodDeclaration, result.get(), argumentAliases);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Phil
  public static LLControlFlowGraph buildInternalCallExpression(HLInternalCallExpression internalCallExpression, LLMethodDeclaration methodDeclaration, Optional<LLDeclaration> result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    if (internalCallExpression.shouldInline()) {
      final HLMethodDeclaration inlineMethodDeclaration = internalCallExpression.getDeclaration();

      final Map<HLScalarFieldDeclaration, LLAliasDeclaration> newArgumentAliases = new HashMap<>();

      for (int i = 0; i < internalCallExpression.getArguments().size(); i++) {
        final HLArgumentDeclaration argumentDeclaration = inlineMethodDeclaration.getBody().getArgumentDeclarations().get(i);
        final HLArgument argument = internalCallExpression.getArguments().get(i);

        final LLAliasDeclaration argumentResult = methodDeclaration.newAlias();
        final LLControlFlowGraph argumentCFG = LLBuilder.buildArgument(argument, methodDeclaration, argumentResult, argumentAliases);
        resultCFG = resultCFG.concatenate(argumentCFG);

        newArgumentAliases.put(argumentDeclaration, argumentResult);
      }

      final LLBasicBlock returnTarget = new LLBasicBlock();
      LLControlFlowGraph bodyCFG = buildBlock(inlineMethodDeclaration.getBody(), methodDeclaration, Optional.empty(), Optional.empty(), newArgumentAliases, result, Optional.of(returnTarget));

      if (bodyCFG.expectExit() != returnTarget) {
        if (inlineMethodDeclaration.getMethodType() != MethodType.VOID) {
          bodyCFG = bodyCFG.concatenate(
            new LLException(LLException.Type.NoReturnValue)
          );
        }
        bodyCFG = bodyCFG.concatenate(returnTarget);
      }

      resultCFG = resultCFG.concatenate(bodyCFG);
    } else {
      final LLMethodDeclaration declaration = internalCallExpression.getDeclaration().getLL();
      final LLInternalCall.Builder builder = new LLInternalCall.Builder(declaration, result);

      for (HLArgument argument : internalCallExpression.getArguments()) {
        final LLAliasDeclaration argumentResult = methodDeclaration.newAlias();
        final LLControlFlowGraph argumentCFG = LLBuilder.buildArgument(argument, methodDeclaration, argumentResult, argumentAliases);
        resultCFG = resultCFG.concatenate(argumentCFG);

        builder.addArgument(argumentResult);
      }

      resultCFG = resultCFG.concatenate(
        builder.build()
      );
    }

    return resultCFG;
  }

  // DONE: Robert
  public static LLControlFlowGraph buildExternalCallExpression(HLExternalCallExpression externalCallExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result, Map<HLScalarFieldDeclaration, LLAliasDeclaration> argumentAliases) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLImportDeclaration declaration = externalCallExpression.getDeclaration().getLL();
    final LLExternalCall.Builder builder = new LLExternalCall.Builder(declaration, result);

    for (HLArgument argument : externalCallExpression.getArguments()) {
      final LLAliasDeclaration argumentResult = methodDeclaration.newAlias();
      final LLControlFlowGraph argumentCFG = LLBuilder.buildArgument(argument, methodDeclaration, argumentResult, argumentAliases);
      resultCFG = resultCFG.concatenate(argumentCFG);

      builder.addArgument(argumentResult);
    }

    resultCFG = resultCFG.concatenate(
      builder.build()
    );

    return resultCFG;
  }

  // DONE: Noah
  public static LLControlFlowGraph buildLengthExpression(HLLengthExpression lengthExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    resultCFG = resultCFG.concatenate(
        new LLLength(lengthExpression.getDeclaration().getLL(), result)
    );

    return resultCFG;
  }

  public static LLControlFlowGraph buildIntegerLiteral(HLIntegerLiteral integerLiteral, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    resultCFG = resultCFG.concatenate(
      new LLIntegerLiteral(integerLiteral.getValue(), result)
    );

    return resultCFG;
  }

  // DONE: Phil
  public static LLControlFlowGraph buildStringLiteral(HLStringLiteral stringLiteral, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();
    LLStringLiteralDeclaration llDeclaration = stringLiteral.getDeclaration().getLL();

    resultCFG = resultCFG.concatenate(
      new LLStringLiteral(llDeclaration, result)
    );

    return resultCFG;
  }

}

