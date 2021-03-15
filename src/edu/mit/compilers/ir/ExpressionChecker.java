package edu.mit.compilers.ir;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

class ExpressionChecker implements ASTExpression.Visitor<VariableType> {

  private final SymbolTable symbolTable;
  
  public ExpressionChecker(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }

  public VariableType visit(ASTBinaryExpression binaryExpression) {
    return binaryExpression.returnType();
  }

  public VariableType visit(ASTUnaryExpression unaryExpression) {
    if (unaryExpression.getType().equals(ASTUnaryExpression.Type.NOT)) {
      return VariableType.BOOLEAN;
    } else /* if (unaryExpression.getType().equals(ASTUnaryExpression.Type.NEGATE)) */ {
      return VariableType.INTEGER;
    }
  }

  public VariableType visit(ASTLocationExpression locationExpression) {
    final String locationId = locationExpression.getIdentifier();
    if (symbolTable.scalarExists(locationId)) {
      return symbolTable.scalarType(locationId);
    } else if (symbolTable.arrayExists(locationId)) {
      return symbolTable.arrayType(locationId);
    } else {
      throw new RuntimeException("should never get here");
    }
  }

  public VariableType visit(ASTMethodCallExpression methodCallExpression) {
    String identifier = methodCallExpression.getIdentifier();

    if (symbolTable.importExists(identifier)) {
      return VariableType.INTEGER;
    } else /* if (symbolTable.methodExists(identifier)) */ {
      // NOTE: ProgramChecker checks that returnType is not void
      return symbolTable.methodReturnType(identifier).toVariableType();
    }
  }

  public VariableType visit(ASTLengthExpression lengthExpression) {
    return VariableType.INTEGER;
  }

  public VariableType visit(ASTIntegerLiteral integerLiteral) {
    return VariableType.INTEGER;
  }

  public VariableType visit(ASTCharacterLiteral characterLiteral) {
    return VariableType.INTEGER;
  }

  public VariableType visit(ASTBooleanLiteral booleanLiteral) {
    return VariableType.BOOLEAN;
  }

}
