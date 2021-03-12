package edu.mit.compilers.ast;

public interface ASTArgument extends ASTNode {

  public static interface Visitor {

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

  public void accept(ASTArgument.Visitor visitor);

}
