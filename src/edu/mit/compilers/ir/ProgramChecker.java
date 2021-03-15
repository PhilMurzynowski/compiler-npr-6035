package edu.mit.compilers.ir;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

import javax.print.attribute.standard.OrientationRequested;

public class ProgramChecker implements ASTNode.Visitor<List<SemanticException>> {

  private final SymbolTable symbolTable;
  private final boolean inLoop;
  private final Optional<MethodType> returnType;

  public ProgramChecker(SymbolTable symbolTable, boolean inLoop, Optional<MethodType> returnType) {
    this.symbolTable = symbolTable;
    this.inLoop = inLoop;
    this.returnType = returnType;
  }

  public List<SemanticException> visit(ASTProgram program) {
    final List<SemanticException> exceptions = new ArrayList<>();

		// not creating symbol table
		for (ASTImportDeclaration importDeclaration : program.getImportDeclarations()) {
			exceptions.addAll(importDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType)));
		}
		for (ASTFieldDeclaration fieldDeclaration : program.getFieldDeclarations()) {
			exceptions.addAll(fieldDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType)));
		}
		boolean hasValidMainDeclaration = false;
		for (ASTMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
			exceptions.addAll(methodDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType)));
			// check for valid main function
			if (methodDeclaration.getIdentifier().equals("main")
			 && methodDeclaration.getMethodType() == MethodType.VOID
			 && methodDeclaration.getArguments().isEmpty()) {
					hasValidMainDeclaration = true;	
			}
		}
		if (!hasValidMainDeclaration) {	
			exceptions.add(new SemanticException(SemanticException.Type.UNDEFINED_MAIN, "missing declaration of main method in program"));
		}
		return exceptions;
  }

  public List<SemanticException> visit(ASTImportDeclaration importDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = importDeclaration.getIdentifier();

    if (symbolTable.exists(identifier)) {
      exceptions.add(new SemanticException(SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
    } else {
      symbolTable.addImport(identifier);
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTFieldDeclaration fieldDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final VariableType type = fieldDeclaration.getType();
    for (ASTFieldDeclaration.Identifier identifier : fieldDeclaration.getIdentifiers()) {
      // duplicate symbol check
      if (symbolTable.exists(identifier.getIdentifier())) {
        exceptions.add(new SemanticException(SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
      } else {
        final Optional<ASTIntegerLiteral> length = identifier.getLength();
        // check array index and add to array symbols
        if (length.isPresent()) {
          // TODO: check that array length is > 0 (rule 4)
          final List<SemanticException> lengthExceptions = length.get().accept(new ProgramChecker(symbolTable, inLoop, returnType));
          exceptions.addAll(lengthExceptions);
          symbolTable.addArray(identifier.getIdentifier(), type);
        // add to scalar symbols
        } else {
          symbolTable.addScalar(identifier.getIdentifier(), type);
        }
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTMethodDeclaration methodDeclaration) {
		final List<SemanticException> exceptions = new ArrayList<>();

		final String identifier = methodDeclaration.getIdentifier();
		final ASTBlock block = methodDeclaration.getBlock();
		final Optional<MethodType> type = Optional.of(methodDeclaration.getMethodType());

		if (symbolTable.exists(identifier)) {
			exceptions.add(new SemanticException(SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
		}
		exceptions.addAll(block.accept(new ProgramChecker(symbolTable, inLoop, type))); // pass in new type

		return exceptions;
  }

  public List<SemanticException> visit(ASTBlock block) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final SymbolTable blockSymbolTable = new SymbolTable(symbolTable);

    for (ASTFieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
      exceptions.addAll(fieldDeclaration.accept(new ProgramChecker(blockSymbolTable, inLoop, returnType)));
    }

    for (ASTStatement statement : block.getStatements()) {
      exceptions.addAll(statement.accept(new ProgramChecker(blockSymbolTable, inLoop, returnType)));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTIDAssignStatement idAssignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    Optional<VariableType> identifierType = Optional.empty();
    Optional<VariableType> expressionType = Optional.empty();

    // scalar identifier exists
    final String identifier = idAssignStatement.getIdentifier();
    if (symbolTable.scalarExists(identifier)) {
      identifierType = Optional.of(symbolTable.scalarType(identifier));
    } else {
      exceptions.add(new SemanticException(SemanticException.Type.UNDEFINED_IDENTIFIER, "invalid scalar identifier " + identifier));
    }

    // expression is valid
    final ASTExpression expression = idAssignStatement.getExpression();
    final List<SemanticException> exprExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(exprExceptions);
    if (exprExceptions.isEmpty()) {
      expressionType = Optional.of(expression.accept(new ExpressionChecker(symbolTable)));
    }

    // identifier type matches expression type
    if (
        identifierType.isPresent() && expressionType.isPresent()
        && !(identifierType.get().equals(expressionType.get()))
    ) {
      exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "assign statement requires same type for location and evaluated expression"));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTAssignStatement assignStatement) {
		final List<SemanticException> exceptions = new ArrayList<>();

		ASTLocationExpression location = assignStatement.getLocation();
		ASTExpression expression = assignStatement.getExpression();

		final List<SemanticException> locationExceptions = location.accept(new ProgramChecker(symbolTable, inLoop, returnType));
		final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType));
		exceptions.addAll(locationExceptions);

		if (locationExceptions.isEmpty() && expressionExceptions.isEmpty()) {
      final VariableType locationType = location.accept(new ExpressionChecker(symbolTable));
      final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));
        if (!expressionType.equals(locationType)) {
          exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "assign statement requries same type for location and evaluated expression"));
				}
		}

		return exceptions;
  }

  public List<SemanticException> visit(ASTCompoundAssignStatement compoundAssignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTLocationExpression location = compoundAssignStatement.getLocation();

    final List<SemanticException> locationExceptions = location.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(locationExceptions);

    if (locationExceptions.isEmpty()) {
      final VariableType locationType = location.accept(new ExpressionChecker(symbolTable));

      if (!locationType.equals(VariableType.INTEGER)) {
        exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "left side of compound assign statment requires an integer"));
      }
    }

    if (compoundAssignStatement.getExpression().isPresent()) {
      final ASTExpression expression = compoundAssignStatement.getExpression().get();

      final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType));
      exceptions.addAll(expressionExceptions);

      if (expressionExceptions.isEmpty()) {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.INTEGER)) {
          exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "right side of compound assign statement requires an integer"));
        }
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTMethodCallStatement methodCallStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // relies completely on ASTMethodCallExpression
    exceptions.addAll(methodCallStatement.getCall().accept(new ProgramChecker(symbolTable, inLoop, returnType)));

    return exceptions;
  }

  public List<SemanticException> visit(ASTIfStatement ifStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression condition = ifStatement.getCondition();
		final ASTBlock body = ifStatement.getBody();
		final Optional<ASTBlock> other = ifStatement.getOther();

    final List<SemanticException> conditionExceptions = condition.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(conditionExceptions);

    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));

      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "if condition expression requires a boolean"));
      }
    }

    exceptions.addAll(body.accept(new ProgramChecker(symbolTable, true, returnType)));
		if (other.isPresent()) {
			exceptions.addAll(other.get().accept(new ProgramChecker(symbolTable, true, returnType)));
		}


		return exceptions;
  }

  public List<SemanticException> visit(ASTForStatement forStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    exceptions.addAll(forStatement.getInitial().accept(new ProgramChecker(symbolTable, inLoop, returnType)));

    final ASTExpression condition = forStatement.getCondition();

    final List<SemanticException> conditionExceptions = condition.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(conditionExceptions);

    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));

      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "for loop condition expression requires a boolean"));
      }
    }

    exceptions.addAll(forStatement.getUpdate().accept(new ProgramChecker(symbolTable, inLoop, returnType)));

    exceptions.addAll(forStatement.getBody().accept(new ProgramChecker(symbolTable, true, returnType)));

    return exceptions;
  }

  public List<SemanticException> visit(ASTWhileStatement whileStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // verify condition expression is valid and evaluates to boolean
    final ASTExpression condition = whileStatement.getCondition();
    final List<SemanticException> conditionExceptions =  condition.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(conditionExceptions);
    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));
      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "while loop condition expression requires a boolean"));
      }
    }

    // collect semantic errors from body
    final ASTBlock body = whileStatement.getBody();
    exceptions.addAll(body.accept(new ProgramChecker(symbolTable, true, returnType)));

    return exceptions;
  }

  public List<SemanticException> visit(ASTReturnStatement returnStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();
		Optional<ASTExpression> expression = returnStatement.getExpression();

		List<SemanticException> expressionExceptions = new ArrayList<SemanticException>();

		if (expression.isPresent()) {
			expressionExceptions = expression.get().accept(new ProgramChecker(symbolTable, inLoop, returnType));
			exceptions.addAll(expressionExceptions);
		}

		if (!returnType.isPresent()) {
      exceptions.add(new SemanticException(SemanticException.Type.INVALID_KEYWORD, "return keyword not allowed outside of a method declaration"));
		} else {

			if (!expression.isPresent() && returnType.get() != MethodType.VOID) {
				exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "must return expression for method not of type void"));

			} else if (expression.isPresent() && expressionExceptions.isEmpty()) {
					final VariableType expressionType = expression.get().accept(new ExpressionChecker(symbolTable));
					if (!expressionType.name().equals(returnType.get().name())) { // NOTE: not super clean, comparing enums with string conversion
						exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "type of returned expression must match method return type, must not return expression for method of type void"));
					}
			}
		}
		

		return exceptions;
  }

  public List<SemanticException> visit(ASTBreakStatement breakStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException(SemanticException.Type.INVALID_KEYWORD, "break keyword not allowed outside of a loop"));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTContinueStatement continueStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException(SemanticException.Type.INVALID_KEYWORD, "continue keyword not allowed outside of a loop"));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTBinaryExpression binaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression left = binaryExpression.getleft();
    final ASTExpression right = binaryExpression.getright();
		final ASTBinaryExpression.Type type = binaryExpression.getType();

    final List<SemanticException> leftExceptions = left.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    final List<SemanticException> rightExceptions = right.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(leftExceptions);
    exceptions.addAll(rightExceptions);

		if (leftExceptions.isEmpty() && rightExceptions.isEmpty()) {
			
			final VariableType leftType = left.accept(new ExpressionChecker(symbolTable));
			final VariableType rightType = right.accept(new ExpressionChecker(symbolTable));

			final boolean booleanOp = (type == ASTBinaryExpression.Type.OR
															|| type == ASTBinaryExpression.Type.AND
															|| type == ASTBinaryExpression.Type.EQUAL
															|| type == ASTBinaryExpression.Type.NOT_EQUAL);
			
			final boolean integerOp = (type == ASTBinaryExpression.Type.EQUAL
															|| type == ASTBinaryExpression.Type.NOT_EQUAL
															|| type == ASTBinaryExpression.Type.LESS_THAN
															|| type == ASTBinaryExpression.Type.LESS_THAN_OR_EQUAL
															|| type == ASTBinaryExpression.Type.GREATER_THAN
															|| type == ASTBinaryExpression.Type.GREATER_THAN_OR_EQUAL
															|| type == ASTBinaryExpression.Type.ADD
															|| type == ASTBinaryExpression.Type.SUBTRACT
															|| type == ASTBinaryExpression.Type.MULTIPLY
															|| type == ASTBinaryExpression.Type.DIVIDE
															|| type == ASTBinaryExpression.Type.MODULUS);
			
			if (leftType != rightType) {
				exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "types in binary expression must match"));
			} else if (booleanOp && (leftType != VariableType.BOOLEAN)) {
				exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "must use boolean binary operator for boolean expressions"));
			} else if (integerOp && (leftType != VariableType.INTEGER)) {
				exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "must use integer binary operator for integer expressions"));
			}
		}

		return exceptions;
  }

  public List<SemanticException> visit(ASTUnaryExpression unaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression expression = unaryExpression.getExpression();

    final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(expressionExceptions);

    if (expressionExceptions.isEmpty()) {
      final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

      if (!unaryExpression.acceptsType(expressionType)) {
        exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "incorrect input type to unary operator "));
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTLocationExpression locationExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String locationId = locationExpression.getIdentifier();
    // location is scalar variable
    if (symbolTable.scalarExists(locationId)) {
      return exceptions;

    // location is array variable
    } else if (symbolTable.arrayExists(locationId)) {
      // array has offset
      final Optional<ASTExpression> offset = locationExpression.getOffset();
      if (offset.isPresent()) {
        // offset is semantically valid
        final List<SemanticException> offsetExceptions = offset.get().accept(new ProgramChecker(symbolTable, inLoop, returnType));
        exceptions.addAll(offsetExceptions);
        if (offsetExceptions.isEmpty()) {
          // offset evaluates to integer
          final VariableType offsetType = offset.get().accept(new ExpressionChecker(symbolTable));
          if (!offsetType.equals(VariableType.INTEGER)) {
            exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "array index must evaluate to an integer"));
          }
        }
      } else {
        exceptions.add(new SemanticException(SemanticException.Type.MISSING_SYMBOL, "array must have index"));
      }

    } else if (symbolTable.exists(locationId)) {
      exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "invalid location: " + locationId));
    } else {
      exceptions.add(new SemanticException(SemanticException.Type.UNDEFINED_IDENTIFIER, "undefined identifier " + locationId));
    }

    return exceptions;
  }

  // Phil
  public List<SemanticException> visit(ASTMethodCallExpression methodCallExpression) {
		// check if exists
    throw new RuntimeException("not implemented");
  }

  public List<SemanticException> visit(ASTLengthExpression lengthExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = lengthExpression.getIdentifier();

    if (!symbolTable.arrayExists(identifier)) {
      exceptions.add(new SemanticException(SemanticException.Type.UNDEFINED_IDENTIFIER, "undefined identifier " + identifier));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTIntegerLiteral integerLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // check that integer is in range
    // TODO: update ASTIntegerLiteral to tolerate values out of range

    return exceptions;
  }

  public List<SemanticException> visit(ASTCharacterLiteral characterLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();
    return exceptions;
  }

  public List<SemanticException> visit(ASTBooleanLiteral booleanLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();
    
    // NOTE(rbd): Nothing to check.

    return exceptions;
  }

  public List<SemanticException> visit(ASTStringLiteral stringLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // NOTE(nmp): Nothing to check.

    return exceptions;
  }

}
