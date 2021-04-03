package edu.mit.compilers.ll;

import java.util.Base64;
import java.util.Optional;
import java.util.List;

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

  // TODO: Phil
  public static LLImportDeclaration buildImportDeclaration(HLImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Phil
  public static LLGlobalArrayFieldDeclaration buildGlobalArrayFieldDeclaration(HLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
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
    final LLMethodDeclaration llMethodDeclaration = new LLMethodDeclaration(hlMethodDeclaration.getIdentifier());
    hlMethodDeclaration.setLL(llMethodDeclaration);

    // WARN(rbd): The lines above MUST be executed before the lines below. (Think: Recursive method calls.)

    llMethodDeclaration.setBody(LLBuilder.buildBlock(hlMethodDeclaration.getBody(), llMethodDeclaration, Optional.empty(), Optional.empty()));

    return llMethodDeclaration;
  }

  // DONE: Robert
  public static LLArgumentDeclaration buildArgumentDeclaration(HLArgumentDeclaration argumentDeclaration, LLMethodDeclaration methodDeclaration) {
    final LLArgumentDeclaration llArgumentDeclaration = new LLArgumentDeclaration(argumentDeclaration.getIndex());
    argumentDeclaration.setLL(llArgumentDeclaration);
    return llArgumentDeclaration;
  }

  // TODO: Phil
  public static LLLocalScalarFieldDeclaration buildLocalScalarFieldDeclaration(HLLocalScalarFieldDeclaration localScalarFieldDeclaration, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
  public static LLLocalArrayFieldDeclaration buildLocalArrayFieldDeclaration(HLLocalArrayFieldDeclaration localArrayFieldDeclaration, LLMethodDeclaration methodDeclaration) {
    final LLLocalArrayFieldDeclaration declaration = new LLLocalArrayFieldDeclaration(
        localArrayFieldDeclaration.getIndex(),
        localArrayFieldDeclaration.getLength().getValue()
    );
    localArrayFieldDeclaration.setLL(declaration);
    return declaration;
  }

  // DONE: Robert
  public static LLControlFlowGraph buildBlock(HLBlock block, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    for (HLArgumentDeclaration hlArgumentDeclaration : block.getArgumentDeclarations()) {
      final LLArgumentDeclaration llArgumentDeclaration = LLBuilder.buildArgumentDeclaration(hlArgumentDeclaration, methodDeclaration);
      hlArgumentDeclaration.setLL(llArgumentDeclaration);
    }

    for (HLLocalScalarFieldDeclaration hlLocalScalarFieldDeclaration : block.getScalarFieldDeclarations()) {
      final LLLocalScalarFieldDeclaration llLocalScalarFieldDeclaration = LLBuilder.buildLocalScalarFieldDeclaration(hlLocalScalarFieldDeclaration, methodDeclaration);
      hlLocalScalarFieldDeclaration.setLL(llLocalScalarFieldDeclaration);

      methodDeclaration.addScalar(llLocalScalarFieldDeclaration);

      final LLAliasDeclaration zeroResult = methodDeclaration.newAlias();
      resultCFG = resultCFG.concatenate(
        new LLIntegerLiteral(0, zeroResult),
        new LLStoreScalar(hlLocalScalarFieldDeclaration.getLL(), zeroResult)
      );
    }

    for (HLLocalArrayFieldDeclaration hlLocalArrayFieldDeclaration : block.getArrayFieldDeclarations()) {
      final LLLocalArrayFieldDeclaration llLocalArrayFieldDeclaration = LLBuilder.buildLocalArrayFieldDeclaration(hlLocalArrayFieldDeclaration, methodDeclaration);
      hlLocalArrayFieldDeclaration.setLL(llLocalArrayFieldDeclaration);

      methodDeclaration.addArray(llLocalArrayFieldDeclaration);

      final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
      final LLAliasDeclaration lengthResult = methodDeclaration.newAlias();
      final LLAliasDeclaration zeroResult = methodDeclaration.newAlias();

      final LLBasicBlock initialBB = new LLBasicBlock(
        new LLIntegerLiteral(0, indexResult),
        new LLIntegerLiteral(llLocalArrayFieldDeclaration.getLength(), lengthResult),
        new LLIntegerLiteral(0, zeroResult)
      );

      final LLBasicBlock conditionBB = new LLBasicBlock(
        new LLCompare(indexResult, lengthResult)
      );

      final LLBasicBlock bodyBB = new LLBasicBlock(
        new LLStoreArray(llLocalArrayFieldDeclaration, indexResult, zeroResult)
      );

      final LLBasicBlock updateBB = new LLBasicBlock(
        new LLUnary(UnaryExpressionType.INCREMENT, indexResult, indexResult)
      );

      final LLBasicBlock exitBB = new LLBasicBlock();

      initialBB.setTrueTarget(conditionBB);

      conditionBB.setTrueTarget(exitBB);
      conditionBB.setFalseTarget(bodyBB);

      bodyBB.setTrueTarget(updateBB);

      updateBB.setTrueTarget(conditionBB);

      resultCFG = resultCFG.concatenate(
        new LLControlFlowGraph(initialBB, exitBB)
      );
    }

    for (HLStatement statement : block.getStatements()) {
      resultCFG = resultCFG.concatenate(
        LLBuilder.buildStatement(statement, methodDeclaration, breakTarget, continueTarget)
      );

      if (statement instanceof HLReturnStatement || statement instanceof HLBreakStatement || statement instanceof HLContinueStatement) {
        break;
      }
    }

    return resultCFG;
  }

  public static LLControlFlowGraph buildStatement(HLStatement statement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    if (statement instanceof HLStoreStatement storeStatement) {
      return LLBuilder.buildStoreStatement(storeStatement, methodDeclaration);
    } else if (statement instanceof HLCallStatement callStatement) {
      return LLBuilder.buildCallStatement(callStatement, methodDeclaration);
    } else if (statement instanceof HLIfStatement ifStatement) {
      return LLBuilder.buildIfStatement(ifStatement, methodDeclaration, breakTarget, continueTarget);
    } else if (statement instanceof HLForStatement forStatement) {
      return LLBuilder.buildForStatement(forStatement, methodDeclaration);
    } else if (statement instanceof HLWhileStatement whileStatement) {
      return LLBuilder.buildWhileStatement(whileStatement, methodDeclaration);
    } else if (statement instanceof HLReturnStatement returnStatement) {
      return LLBuilder.buildReturnStatement(returnStatement, methodDeclaration);
    } else if (statement instanceof HLBreakStatement breakStatement) {
      return LLBuilder.buildBreakStatement(breakStatement, methodDeclaration, breakTarget, continueTarget);
    } else if (statement instanceof  HLContinueStatement continueStatement) {
      return LLBuilder.buildContinueStatement(continueStatement, methodDeclaration, breakTarget, continueTarget);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildStoreStatement(HLStoreStatement storeStatement, LLMethodDeclaration methodDeclaration) {
    if (storeStatement instanceof HLStoreScalarStatement storeScalarStatement) {
      return LLBuilder.buildStoreScalarStatement(storeScalarStatement, methodDeclaration);
    } else if (storeStatement instanceof HLStoreArrayStatement storeArrayStatement) {
      return LLBuilder.buildStoreArrayStatement(storeArrayStatement, methodDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildStoreScalarStatement(HLStoreScalarStatement storeScalarStatement, LLMethodDeclaration methodDeclaration) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration expressionResult = methodDeclaration.newAlias();
    final LLControlFlowGraph expressionCFG = LLBuilder.buildExpression(storeScalarStatement.getExpression(), methodDeclaration, expressionResult);
    resultCFG = resultCFG.concatenate(expressionCFG);

    resultCFG = resultCFG.concatenate(
      new LLStoreScalar(storeScalarStatement.getDeclaration().getLL(), expressionResult)
    );

    return resultCFG;
  }

  // DONE: Noah
  public static LLControlFlowGraph buildStoreArrayStatement(HLStoreArrayStatement storeArrayStatement, LLMethodDeclaration methodDeclaration) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    // value expression evaluation
    final LLAliasDeclaration valueResult = methodDeclaration.newAlias();
    final LLControlFlowGraph valueCFG = LLBuilder.buildExpression(storeArrayStatement.getExpression(), methodDeclaration, valueResult);
    resultCFG = resultCFG.concatenate(valueCFG);

    // index expression evaluation
    final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
    final LLControlFlowGraph indexCFG = LLBuilder.buildExpression(storeArrayStatement.getIndex(), methodDeclaration, indexResult);
    resultCFG = resultCFG.concatenate(indexCFG);

    resultCFG = resultCFG.concatenate(
      new LLStoreArray(storeArrayStatement.getDeclaration().getLL(), indexResult, valueResult)
    );

    return resultCFG;
  }

  // TODO: Phil
  public static LLControlFlowGraph buildCallStatement(HLCallStatement callStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static LLControlFlowGraph buildIfStatement(HLIfStatement ifStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    final LLBasicBlock exitBB = new LLBasicBlock();

    LLControlFlowGraph bodyCFG = LLBuilder.buildBlock(ifStatement.getBody(), methodDeclaration, breakTarget, continueTarget);
    bodyCFG = bodyCFG.concatenate(exitBB);

    LLControlFlowGraph otherCFG;
    if (ifStatement.getOther().isPresent()) {
      otherCFG = LLBuilder.buildBlock(ifStatement.getOther().get(), methodDeclaration, breakTarget, continueTarget);
    } else {
      otherCFG = LLControlFlowGraph.empty();
    }
    otherCFG = otherCFG.concatenate(exitBB);

    final LLBasicBlock entryBB = LLShortCircuit.shortExpression(ifStatement.getCondition(), methodDeclaration, bodyCFG.getEntry(), otherCFG.getEntry());

    return new LLControlFlowGraph(entryBB, exitBB);
  }

  // DONE: Noah
  public static LLControlFlowGraph buildForStatement(HLForStatement forStatement, LLMethodDeclaration methodDeclaration) {
    LLControlFlowGraph initializeCFG = buildStoreScalarStatement(forStatement.getInitial(), methodDeclaration);
    final LLBasicBlock exitBB = new LLBasicBlock();
    LLControlFlowGraph updateCFG = buildStoreStatement(forStatement.getUpdate(), methodDeclaration);

    LLControlFlowGraph bodyCFG = buildBlock(
        forStatement.getBody(),
        methodDeclaration,
        Optional.of(exitBB),
        Optional.of(updateCFG.getEntry())
    );

    final LLBasicBlock conditionBB = LLShortCircuit.shortExpression(
        forStatement.getCondition(), methodDeclaration, bodyCFG.getEntry(), exitBB
    );

    // connect all the different pieces
    updateCFG = updateCFG.concatenate(conditionBB);
    if (bodyCFG.getExit() == updateCFG.getEntry() || bodyCFG.getExit() == exitBB) {
      bodyCFG = bodyCFG.concatenate(updateCFG);
    }
    conditionBB.setTrueTarget(bodyCFG.getEntry());
    initializeCFG = initializeCFG.concatenate(updateCFG);

    return new LLControlFlowGraph(initializeCFG.getEntry(), exitBB);

  }

  // TODO: Phil
  public static LLControlFlowGraph buildWhileStatement(HLWhileStatement whileStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildReturnStatement(HLReturnStatement returnStatement, LLMethodDeclaration methodDeclaration) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    Optional<HLExpression> hlExpression = returnStatement.getExpression();
    if (hlExpression.isPresent()) {
      final LLAliasDeclaration expressionResult = methodDeclaration.newAlias();
      final LLControlFlowGraph expressionCFG = LLBuilder.buildExpression(hlExpression.get(), methodDeclaration, expressionResult);
      resultCFG = resultCFG.concatenate(expressionCFG);
      resultCFG = resultCFG.concatenate(
        new LLReturn(Optional.of(expressionResult))
      );
    } else {
      resultCFG = resultCFG.concatenate(
        new LLReturn(Optional.empty())
      );
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

  // TODO: Phil
  public static LLControlFlowGraph buildArgument(HLArgument argument, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      return LLBuilder.buildBinaryExpression(binaryExpression, methodDeclaration, result);
    } else if (expression instanceof HLUnaryExpression unaryExpression) {
      return LLBuilder.buildUnaryExpression(unaryExpression, methodDeclaration, result);
    } else if (expression instanceof HLLoadExpression loadExpression) {
      return LLBuilder.buildLoadExpression(loadExpression, methodDeclaration, result);
    } else if (expression instanceof HLCallExpression callExpression) {
      return LLBuilder.buildCallExpression(callExpression, methodDeclaration, result);
    } else if (expression instanceof HLLengthExpression lengthExpression) {
      return LLBuilder.buildLengthExpression(lengthExpression, methodDeclaration, result);
    } else if (expression instanceof HLIntegerLiteral integerLiteral) {
      return LLBuilder.buildIntegerLiteral(integerLiteral, methodDeclaration, result);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildBinaryExpression(HLBinaryExpression binaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration leftResult = methodDeclaration.newAlias();
    final LLControlFlowGraph leftCFG = LLBuilder.buildExpression(binaryExpression.getLeft(), methodDeclaration, leftResult);
    resultCFG = resultCFG.concatenate(leftCFG);

    final LLAliasDeclaration rightResult = methodDeclaration.newAlias();
    final LLControlFlowGraph rightCFG = LLBuilder.buildExpression(binaryExpression.getRight(), methodDeclaration, rightResult);
    resultCFG = resultCFG.concatenate(rightCFG);

    resultCFG = resultCFG.concatenate(
      new LLBinary(leftResult, binaryExpression.getType(), rightResult, result)
    );

    return resultCFG;
  }

  public static LLControlFlowGraph buildUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration unaryResult = methodDeclaration.newAlias();
    final LLControlFlowGraph unaryCFG = LLBuilder.buildExpression(unaryExpression.getExpression(), methodDeclaration, unaryResult);
    resultCFG = resultCFG.concatenate(unaryCFG);

    resultCFG = resultCFG.concatenate(
      new LLUnary(unaryExpression.getType(), unaryResult, result)
    );

    return resultCFG;
  }

  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    if (loadExpression instanceof HLLoadScalarExpression loadScalarExpression) {
      return LLBuilder.buildLoadScalarExpression(loadScalarExpression, result);
    } else if (loadExpression instanceof HLLoadArrayExpression loadArrayExpression) {
      return LLBuilder.buildLoadArrayExpression(loadArrayExpression, methodDeclaration, result);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    resultCFG = resultCFG.concatenate(
      new LLLoadScalar(loadScalarExpression.getDeclaration().getLL(), result)
    );

    return resultCFG;
  }

  // DONE: Robert
  public static LLControlFlowGraph buildLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLAliasDeclaration indexResult = methodDeclaration.newAlias();
    final LLControlFlowGraph indexCFG = LLBuilder.buildExpression(loadArrayExpression.getIndex(), methodDeclaration, indexResult);
    resultCFG = resultCFG.concatenate(indexCFG);

    resultCFG = resultCFG.concatenate(
      new LLLoadArray(loadArrayExpression.getDeclaration().getLL(), indexResult, result)
    );

    return resultCFG;
  }

  // DONE: Noah
  public static LLControlFlowGraph buildCallExpression(HLCallExpression callExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    if (callExpression instanceof HLInternalCallExpression internalCallExpression) {
      return buildInternalCallExpression(internalCallExpression, methodDeclaration, result);
    } else if (callExpression instanceof HLExternalCallExpression externalCallExpression) {
      return buildExternalCallExpression(externalCallExpression, methodDeclaration, result);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // TODO: Phil
  public static LLControlFlowGraph buildInternalCallExpression(HLInternalCallExpression internalCallExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static LLControlFlowGraph buildExternalCallExpression(HLExternalCallExpression externalCallExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    final LLImportDeclaration declaration = externalCallExpression.getDeclaration().getLL();
    final LLExternalCall.Builder builder = new LLExternalCall.Builder(declaration, result);

    for (HLArgument argument : externalCallExpression.getArguments()) {
      final LLAliasDeclaration argumentResult = methodDeclaration.newAlias();
      final LLControlFlowGraph argumentCFG = LLBuilder.buildArgument(argument, methodDeclaration, argumentResult);
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

    resultCFG.concatenate(
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

  // TODO: Phil
  public static LLControlFlowGraph buildStringLiteral(HLStringLiteral stringLiteral, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

}

