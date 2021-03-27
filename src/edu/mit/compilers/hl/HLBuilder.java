package edu.mit.compilers.hl;

import edu.mit.compilers.ast.*;

public class HLBuilder {

  // TODO: Robert
  public static HLProgram buildProgram(HLSymbolTable symbolTable, ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  public static HLImportDeclaration buildImportDeclaration(HLSymbolTable symbolTable, ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static HLGlobalScalarFieldDeclaration buildGlobalScalarFieldDeclaration(HLSymbolTable symbolTable, ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLGlobalArrayFieldDeclaration buildGlobalArrayFieldDeclaration(HLSymbolTable symbolTable, ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLLocalScalarFieldDeclaration buildLocalScalarFieldDeclaration(HLSymbolTable symbolTable, ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLLocalArrayFieldDeclaration buildLocalArrayFieldDeclaration(HLSymbolTable symbolTable, ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLMethodDeclaration buildMethodDeclaration(HLSymbolTable symbolTable, ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static HLBlock buildBlock(HLSymbolTable symbolTable, ASTBlock block) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static HLStatement buildStatement(HLSymbolTable symbolTable, ASTStatement statement) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLStoreScalarStatement buildIDAssignStatement(HLSymbolTable symbolTable, ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLStoreStatement buildAssignStatement(HLSymbolTable symbolTable, ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLStoreStatement buildCompoundAssignStatement(HLSymbolTable symbolTable, ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLCallStatement buildMethodCallStatement(HLSymbolTable symbolTable, ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLIfStatement buildIfStatement(HLSymbolTable symbolTable, ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLForStatement buildForStatement(HLSymbolTable symbolTable, ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLWhileStatement buildWhileStatement(HLSymbolTable symbolTable, ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLReturnStatement buildReturnStatement(HLSymbolTable symbolTable, ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLBreakStatement buildBreakStatement(HLSymbolTable symbolTable, ASTBreakStatement breakStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLContinueStatement buildContinueStatement(HLSymbolTable symbolTable, ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static HLArgument buildArgument(HLSymbolTable symbolTable, ASTArgument argument) {
    if (argument instanceof ASTExpression expression) {
      return buildExpression(symbolTable, expression);
    } else if (argument instanceof ASTStringLiteral stringLiteral) {
      return buildStringLiteral(symbolTable, stringLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // TODO: Robert
  public static HLExpression buildExpression(HLSymbolTable symbolTable, ASTExpression expression) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static HLBinaryExpression buildBinaryExpression(HLSymbolTable symbolTable, ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static HLUnaryExpression buildUnaryExpression(HLSymbolTable symbolTable, ASTUnaryExpression unaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLLoadExpression buildLocationExpression(HLSymbolTable symbolTable, ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLCallExpression buildMethodCallExpression(HLSymbolTable symbolTable, ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLLengthExpression buildLengthExpression(HLSymbolTable symbolTable, ASTLengthExpression lengthExpression) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static HLIntegerLiteral buildIntegerLiteral(HLSymbolTable symbolTable, ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLIntegerLiteral buildCharacterLiteral(HLSymbolTable symbolTable, ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLIntegerLiteral buildBooleanLiteral(HLSymbolTable symbolTable, ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLStringLiteral buildStringLiteral(HLSymbolTable symbolTable, ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
