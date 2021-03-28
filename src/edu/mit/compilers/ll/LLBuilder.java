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

  // TODO: Robert
  public static LLMethodDeclaration buildMethodDeclaration(HLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Robert
  public static LLControlFlowGraph buildStoreStatement(HLStoreStatement storeStatement, LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Robert
  public static LLControlFlowGraph buildBinaryExpression(HLBinaryExpression binaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildUnaryExpression(HLUnaryExpression unaryExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLControlFlowGraph buildLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLMethodDeclaration methodDeclaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
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

