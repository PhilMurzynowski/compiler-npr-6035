package edu.mit.compilers.ir;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

class ExpressionChecker implements ASTExpression.Visitor<VariableType> {

  private final SymbolTable symbolTable;

  // Noah
  public ExpressionChecker(SymbolTable symbolTable) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public VariableType visit(ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public VariableType visit(ASTUnaryExpression unaryExpression) {
    if (unaryExpression.getType().equals(ASTUnaryExpression.Type.NOT)) {
      return VariableType.BOOLEAN;
    } else /* if (unaryExpression.getType().equals(ASTUnaryExpression.Type.NEGATE)) */ {
      return VariableType.INTEGER;
    }
  }

  // Noah
  public VariableType visit(ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public VariableType visit(ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  public VariableType visit(ASTLengthExpression lengthExpression) {
    return VariableType.INTEGER;
  }

  // Noah
  public VariableType visit(ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public VariableType visit(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public VariableType visit(ASTBooleanLiteral booleanLiteral) {
    return VariableType.BOOLEAN;
  }

}
