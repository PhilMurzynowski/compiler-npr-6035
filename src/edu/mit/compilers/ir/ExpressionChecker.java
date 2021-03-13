package edu.mit.compilers.ir;

import edu.mit.compilers.ast.*;

class ExpressionChecker implements ASTExpression.Visitor {

  // TODO(rbd): Add representation.

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

  // TODO(rbd): Add result observers.

}
