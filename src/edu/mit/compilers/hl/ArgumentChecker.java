
package edu.mit.compilers.hl;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

class ArgumentChecker implements ASTArgument.Visitor<Either<ASTExpression, SemanticException>> {

  private final SymbolTable symbolTable;

  public ArgumentChecker(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }

  public Either<ASTExpression, SemanticException> visit(ASTBinaryExpression binaryExpression) {
    return Either.left(binaryExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTUnaryExpression unaryExpression) {
    return Either.left(unaryExpression);
  }

  public Either<ASTExpression, SemanticException> visit(ASTLocationExpression locationExpression) {
    final String locationId = locationExpression.getIdentifier();
    if (symbolTable.scalarExists(locationId)) {
      return Either.left(locationExpression);
    } else if (symbolTable.arrayExists(locationId)) {
      if (locationExpression.getOffset().isPresent()) {
        return Either.left(locationExpression);
      } else {
        return Either.right(new SemanticException(locationExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "Invalid array argument for declared method in method call"));
      }
    } else {
      throw new RuntimeException("should never get here");
    }
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
