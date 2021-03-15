
package edu.mit.compilers.ir;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

class ArgumentChecker implements ASTArgument.Visitor<Either<ASTExpression, SemanticException>> {

  public ArgumentChecker() { }

  public Either<ASTExpression, SemanticException> visit(ASTBinaryExpression binaryExpression) {
    return Either.left(binaryExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTUnaryExpression unaryExpression) {
    return Either.left(unaryExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTLocationExpression locationExpression) {
    return Either.left(locationExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTMethodCallExpression methodCallExpression) {
    return Either.left(methodCallExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTLengthExpression lengthExpression) {
    return Either.left(lengthExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTIntegerLiteral integerLiteral) {
    return Either.left(integerLiteral);
  }

  public Either<ASTExpression, SemanticException> visit(ASTCharacterLiteral characterLiteral) {
    return Either.left(characterLiteral);
  }

  public Either<ASTExpression, SemanticException> visit(ASTBooleanLiteral booleanLiteral) {
    return Either.left(booleanLiteral);
  }

  public Either<ASTExpression, SemanticException> visit(ASTStringLiteral stringLiteral) {
    return Either.right(new SemanticException(stringLiteral.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "Invalid string argument for declared method in method call"));
  }

}
