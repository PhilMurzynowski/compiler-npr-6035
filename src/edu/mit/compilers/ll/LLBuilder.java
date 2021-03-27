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
  public static LLControlFlowGraph buildStatement(HLStatement statement, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLControlFlowGraph buildStoreStatement(HLStoreStatement storeStatement) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildStoreScalarStatement(HLStoreScalarStatement scalarStoreStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildStoreArrayStatement(HLStoreArrayStatement arrayStoreStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildCallStatement(HLCallStatement callStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildIfStatement(HLIfStatement ifStatement, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildForStatement(HLForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildWhileStatement(HLWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildReturnStatement(HLReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildBreakStatement(HLBreakStatement breakStatement, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildContinueStatement(HLContinueStatement continueStatement, Optional<LLBasicBlock> breakTarget, Optional<LLBasicBlock> continueTarget) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildArgument(HLArgument argument, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static LLControlFlowGraph buildExpression(HLExpression expression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLControlFlowGraph buildBinaryExpression(HLBinaryExpression binaryExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildUnaryExpression(HLUnaryExpression unaryExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static LLControlFlowGraph buildLoadExpression(HLLoadExpression loadScalarExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static LLControlFlowGraph buildLoadScalarExpression(HLLoadScalarExpression loadScalarExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildLoadArrayExpression(HLLoadArrayExpression loadArrayExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildCallExpression(HLCallExpression callExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildInternalCallExpression(HLInternalCallExpression internalCallExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildExternalCallExpression(HLExternalCallExpression externalCallExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildLengthExpression(HLLengthExpression lengthExpression, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static LLControlFlowGraph buildIntegerLiteral(HLIntegerLiteral integerLiteral, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public static LLControlFlowGraph buildStringLiteral(HLStringLiteral stringLiteral, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

}

