package edu.mit.compilers.ll;

import java.util.Optional;

import edu.mit.compilers.hl.*;

public class LLBuilder {

  // TODO: Phil
  public static LLProgram buildProgram(HLProgram program) {
    throw new RuntimeException("not implemented");
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

  // TODO: Noah
  public static LLGlobalScalarFieldDeclaration buildGlobalScalarFieldDeclaration(HLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Noah
  public static LLControlFlowGraph buildStatement(HLStatement statement, LLMethodDeclaration methodDeclaration, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
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

  // TODO: Noah
  public static LLControlFlowGraph buildExpression(HLExpression expression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
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

  // TODO: Noah
  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
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

