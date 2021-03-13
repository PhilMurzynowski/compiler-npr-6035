package edu.mit.compilers.ast;

public interface ASTArgument extends ASTNode {

  public static interface Visitor<T> {

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

  public <T> T accept(ASTArgument.Visitor<T> visitor);

}
