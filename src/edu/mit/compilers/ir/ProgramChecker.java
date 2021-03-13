package edu.mit.compilers.ir;

import edu.mit.compilers.ast.*;

public class ProgramChecker implements ASTNode.Visitor {

  // TODO(rbd): Add representation.

  public void visit(ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTBlock block) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTBreakStatement breakStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTUnaryExpression unaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTLengthExpression lengthExpression) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  public void visit(ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

  // TODO(rbd): Add result observers.

}
