package edu.mit.compilers.ast;

public interface ASTNode {

  public static interface Visitor<T> {
    
    T visit(ASTProgram program);

    T visit(ASTImportDeclaration importDeclaration);

    T visit(ASTFieldDeclaration fieldDeclaration);

    T visit(ASTMethodDeclaration methodDeclaration);

    T visit(ASTBlock block);

    T visit(ASTIDAssignStatement idAssignStatement);

    T visit(ASTAssignStatement assignStatement);

    T visit(ASTCompoundAssignStatement compoundAssignStatement);

    T visit(ASTMethodCallStatement methodCallStatement);

    T visit(ASTIfStatement ifStatement);

    T visit(ASTForStatement forStatement);

    T visit(ASTWhileStatement whileStatement);

    T visit(ASTReturnStatement returnStatement);

    T visit(ASTBreakStatement breakStatement);

    T visit(ASTContinueStatement continueStatement);

    T visit(ASTBinaryExpression binaryExpression);

    T visit(ASTUnaryExpression unaryExpression);

    T visit(ASTLocationExpression locationExpression);

    T visit(ASTMethodCallExpression methodCallExpression);

    T visit(ASTLengthExpression lengthExpression);

    T visit(ASTIntegerLiteral integerLiteral);

    T visit(ASTCharacterLiteral characterLiteral);

    T visit(ASTBooleanLiteral booleanLiteral);

    T visit(ASTStringLiteral stringLiteral);

  }

  public <T> T accept(ASTNode.Visitor<T> visitor);

  public String prettyString(int depth);

  public String debugString(int depth);

  @Override
  public String toString();

  @Override
  public boolean equals(Object that);

  @Override
  public int hashCode();

}
