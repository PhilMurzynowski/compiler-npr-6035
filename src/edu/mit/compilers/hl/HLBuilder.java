package edu.mit.compilers.hl;

import edu.mit.compilers.ast.*;

public class HLBuilder {
  
  public static HLNode buildProgram(HLSymbolTable symbolTable, ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildImportDeclaration(HLSymbolTable symbolTable, ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildFieldDeclaration(HLSymbolTable symbolTable, ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodDeclaration(HLSymbolTable symbolTable, ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBlock(HLSymbolTable symbolTable, ASTBlock block) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildStatement(HLSymbolTable symbolTable, ASTStatement statement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildIDAssignStatement(HLSymbolTable symbolTable, ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildAssignStatement(HLSymbolTable symbolTable, ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildCompoundAssignStatement(HLSymbolTable symbolTable, ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodCallStatement(HLSymbolTable symbolTable, ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildIfStatement(HLSymbolTable symbolTable, ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildForStatement(HLSymbolTable symbolTable, ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildWhileStatement(HLSymbolTable symbolTable, ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildReturnStatement(HLSymbolTable symbolTable, ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBreakStatement(HLSymbolTable symbolTable, ASTBreakStatement breakStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildContinueStatement(HLSymbolTable symbolTable, ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildArgument(HLSymbolTable symbolTable, ASTArgument argument) {
    if (argument instanceof ASTExpression expression) {
      return buildExpression(symbolTable, expression);
    } else if (argument instanceof ASTStringLiteral stringLiteral) {
      return buildStringLiteral(symbolTable, stringLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static HLNode buildExpression(HLSymbolTable symbolTable, ASTExpression expression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBinaryExpression(HLSymbolTable symbolTable, ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildUnaryExpression(HLSymbolTable symbolTable, ASTUnaryExpression unaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildLocationExpression(HLSymbolTable symbolTable, ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodCallExpression(HLSymbolTable symbolTable, ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildLengthExpression(HLSymbolTable symbolTable, ASTLengthExpression lengthExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildIntegerLiteral(HLSymbolTable symbolTable, ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildCharacterLiteral(HLSymbolTable symbolTable, ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBooleanLiteral(HLSymbolTable symbolTable, ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildStringLiteral(HLSymbolTable symbolTable, ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
