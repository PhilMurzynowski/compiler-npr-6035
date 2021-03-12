package edu.mit.compilers.ast;

public interface ASTExpression extends ASTArgument {

  public static interface Visitor {

    void visit(ASTBinaryExpression binaryExpression);

    void visit(ASTUnaryExpression unaryExpression);

    void visit(ASTLocationExpression locationExpression);

    void visit(ASTMethodCallExpression methodCallExpression);

    void visit(ASTLengthExpression lengthExpression);

    void visit(ASTIntegerLiteral integerLiteral);

    void visit(ASTCharacterLiteral characterLiteral);

    void visit(ASTBooleanLiteral booleanLiteral);

  }

  public void accept(ASTExpression.Visitor visitor);

}
