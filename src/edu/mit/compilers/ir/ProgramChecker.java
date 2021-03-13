package edu.mit.compilers.ir;

import java.util.List;

import edu.mit.compilers.ast.*;

public class ProgramChecker implements ASTNode.Visitor<List<SemanticException>> {

  private final SymbolTable symbolTable;
  private final boolean inLoop;

  // Noah
  public ProgramChecker(SymbolTable symbolTable, boolean inLoop) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTBlock block) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTBreakStatement breakStatement) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTUnaryExpression unaryExpression) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTLengthExpression lengthExpression) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<SemanticException> visit(ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
