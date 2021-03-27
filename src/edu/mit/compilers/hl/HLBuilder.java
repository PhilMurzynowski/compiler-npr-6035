package edu.mit.compilers.hl;

import edu.mit.compilers.ast.*;

public class HLBuilder {
  
  public static HLNode buildProgram(ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildImportDeclaration(ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildFieldDeclaration(ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBlock(ASTBlock block) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildStatement(ASTStatement statement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildIDAssignStatement(ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildAssignStatement(ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildCompoundAssignStatement(ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodCallStatement(ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildIfStatement(ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildForStatement(ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildWhileStatement(ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildReturnStatement(ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBreakStatement(ASTBreakStatement breakStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildContinueStatement(ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildArgument(ASTArgument argument) {
    if (argument instanceof ASTExpression expression) {
      return buildExpression(expression);
    } else if (argument instanceof ASTStringLiteral stringLiteral) {
      return buildStringLiteral(stringLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static HLNode buildExpression(ASTExpression expression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBinaryExpression(ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildUnaryExpression(ASTUnaryExpression unaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildLocationExpression(ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildMethodCallExpression(ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildLengthExpression(ASTLengthExpression lengthExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public static HLNode buildIntegerLiteral(ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildCharacterLiteral(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildBooleanLiteral(ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static HLNode buildStringLiteral(ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
