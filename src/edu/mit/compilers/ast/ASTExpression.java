package edu.mit.compilers.ast;

public interface ASTExpression extends ASTArgument {

  public static interface Visitor<T> {

    T visit(ASTBinaryExpression binaryExpression);

    T visit(ASTUnaryExpression unaryExpression);

    T visit(ASTLocationExpression locationExpression);

    T visit(ASTMethodCallExpression methodCallExpression);

    T visit(ASTLengthExpression lengthExpression);

    T visit(ASTIntegerLiteral integerLiteral);

    T visit(ASTCharacterLiteral characterLiteral);

    T visit(ASTBooleanLiteral booleanLiteral);

  }

  public <T> T accept(ASTExpression.Visitor<T> visitor);

}
