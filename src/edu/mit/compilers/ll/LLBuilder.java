package edu.mit.compilers.ll;

import java.util.Optional;

import edu.mit.compilers.hl.*;

public class LLBuilder {

  // DONE: Phil
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

  public static LLImportDeclaration buildImportDeclaration(HLImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLScalarFieldDeclaration buildScalarFieldDeclaration(HLScalarFieldDeclaration scalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLArrayFieldDeclaration buildArrayFieldDeclaration(HLArrayFieldDeclaration arrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
  public static LLGlobalScalarFieldDeclaration buildGlobalScalarFieldDeclaration(HLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    final LLGlobalScalarFieldDeclaration llGlobalScalarFieldDeclaration = new LLGlobalScalarFieldDeclaration(globalScalarFieldDeclaration.getIdentifier());
    globalScalarFieldDeclaration.setLL(llGlobalScalarFieldDeclaration);
    return llGlobalScalarFieldDeclaration;
  }

  public static LLGlobalArrayFieldDeclaration buildGlobalArrayFieldDeclaration(HLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLStringLiteralDeclaration buildStringLiteralDeclaration(HLStringLiteralDeclaration stringLiteralDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLMethodDeclaration buildMethodDeclaration(HLMethodDeclaration hlMethodDeclaration) {
    final LLMethodDeclaration llMethodDeclaration = new LLMethodDeclaration(hlMethodDeclaration.getIdentifier());
    hlMethodDeclaration.setLL(llMethodDeclaration);

    // WARN(rbd): The lines above MUST be executed before the lines below. (Think recursive method calls.)

    llMethodDeclaration.setBody(LLBuilder.buildBlock(hlMethodDeclaration.getBody(), llMethodDeclaration, Optional.empty(), Optional.empty()));

    return llMethodDeclaration;
  }

  public static LLArgumentDeclaration buildArgumentDeclaration(HLArgumentDeclaration argumentDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLLocalScalarFieldDeclaration buildLocalScalarFieldDeclaration(HLLocalScalarFieldDeclaration globalScalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLLocalArrayFieldDeclaration buildLocalArrayFieldDeclaration(HLLocalArrayFieldDeclaration globalArrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildBlock(HLBlock block, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    // NOTE(rbd): mutate methodDeclaration for hoisting from HLBlock
    // NOTE(rbd): be sure to update indices for field declaration descriptors (must mutate the HL with forwarding to LL)
    // NOTE(rbd): reset to zero all declarations
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
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

  // TODO: Phil
  public static LLControlFlowGraph buildStoreScalarStatement(HLStoreScalarStatement scalarStoreStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildStoreArrayStatement(HLStoreArrayStatement arrayStoreStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildCallStatement(HLCallStatement callStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildIfStatement(HLIfStatement ifStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildForStatement(HLForStatement forStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildWhileStatement(HLWhileStatement whileStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildReturnStatement(HLReturnStatement returnStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildBreakStatement(HLBreakStatement breakStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildContinueStatement(HLContinueStatement continueStatement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildArgument(HLArgument argument, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
  public static LLControlFlowGraph buildExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    if (expression instanceof HLBinaryExpression binaryExpression) {
      return LLBuilder.buildBinaryExpression(binaryExpression, methodDeclaration, result);
    } else if (expression instanceof  HLUnaryExpression unaryExpression) {
      return LLBuilder.buildUnaryExpression(unaryExpression, methodDeclaration, result);
    } else if (expression instanceof HLLoadExpression loadExpression) {
      return LLBuilder.buildLoadExpression(loadExpression, methodDeclaration, result);
    } else if (expression instanceof HLCallExpression callExpression) {
      return LLBuilder.buildCallExpression(callExpression, methodDeclaration, result);
    } else if (expression instanceof HLLengthExpression lengthExpression) {
      return LLBuilder.buildLengthExpression(lengthExpression, methodDeclaration, result);
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

  // TODO: Phil
  public static LLControlFlowGraph buildUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    if (loadExpression instanceof HLLoadScalarExpression loadScalarExpression) {
      return LLBuilder.buildLoadScalarExpression(loadScalarExpression, methodDeclaration, result);
    } else if (loadExpression instanceof HLLoadArrayExpression loadArrayExpression) {
      return LLBuilder.buildLoadArrayExpression(loadArrayExpression, methodDeclaration, result);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static LLControlFlowGraph buildLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    LLControlFlowGraph resultCFG = LLControlFlowGraph.empty();

    resultCFG = resultCFG.concatenate(
      new LLLoadScalar(loadScalarExpression.getDeclaration().getLL(), result)
    );

    return resultCFG;
  }

  public static LLControlFlowGraph buildLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildCallExpression(HLCallExpression callExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildInternalCallExpression(HLInternalCallExpression internalCallExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildExternalCallExpression(HLExternalCallExpression externalCallExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildLengthExpression(HLLengthExpression lengthExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildIntegerLiteral(HLIntegerLiteral integerLiteral, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildStringLiteral(HLStringLiteral stringLiteral, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

}

