package edu.mit.compilers.ast;

public interface ASTNode {

  public static interface Visitor {
    
    void visit(ASTProgram program);
    
    void visit(ASTImportDeclaration importDeclaration);
    
    void visit(ASTFieldDeclaration fieldDeclaration);
    
    void visit(ASTMethodDeclaration methodDeclaration);

    void visit(ASTBlock block);

    void visit(ASTIDAssignStatement idAssignStatement);

    void visit(ASTAssignStatement assignStatement);

    void visit(ASTCompoundAssignStatement compoundAssignStatement);

    void visit(ASTMethodCallStatement methodCallStatement);

    void visit(ASTIfStatement ifStatement);

    void visit(ASTForStatement forStatement);

    void visit(ASTWhileStatement whileStatement);

    void visit(ASTReturnStatement returnStatement);

    void visit(ASTBreakStatement breakStatement);

    void visit(ASTContinueStatement continueStatement);

    void visit(ASTBinaryExpression binaryExpression);

    void visit(ASTUnaryExpression unaryExpression);

    void visit(ASTLocationExpression locationExpression);

    void visit(ASTMethodCallExpression methodCallExpression);

    void visit(ASTLengthExpression lengthExpression);

    void visit(ASTIntegerLiteral integerLiteral);

    void visit(ASTCharacterLiteral characterLiteral);

    void visit(ASTBooleanLiteral booleanLiteral);

    void visit(ASTStringLiteral stringLiteral);

  }

  public void accept(ASTNode.Visitor visitor);

  public String prettyString(int depth);

  public String debugString(int depth);

  @Override
  public String toString();

  @Override
  public boolean equals(Object that);

  @Override
  public int hashCode();

}
