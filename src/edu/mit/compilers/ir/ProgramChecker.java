package edu.mit.compilers.ir;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

public class ProgramChecker implements ASTNode.Visitor<List<SemanticException>> {

  private final SymbolTable symbolTable;
  private final boolean inLoop;
  private final Optional<MethodType> returnType;

  // Noah
  public ProgramChecker(SymbolTable symbolTable, boolean inLoop, Optional<MethodType> returnType) {
    throw new RuntimeException("not implemented");
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

  // Noah
  public List<SemanticException> visit(ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
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

  // Noah
  public List<SemanticException> visit(ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
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

  // Noah
  public List<SemanticException> visit(ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTIfStatement ifStatement) {
    throw new RuntimeException("not implemented");
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

  // Noah
  public List<SemanticException> visit(ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTReturnStatement returnStatement) {
    throw new RuntimeException("not implemented");
  }

  public List<SemanticException> visit(ASTBreakStatement breakStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException(SemanticException.Type.INVALID_KEYWORD, "break keyword not allowed outside of a loop"));
    }

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTBinaryExpression binaryExpression) {
    throw new RuntimeException("not implemented");
  }

  public List<SemanticException> visit(ASTUnaryExpression unaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression expression = unaryExpression.getExpression();

    final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType));
    exceptions.addAll(expressionExceptions);

    if (expressionExceptions.isEmpty()) {
      final ASTUnaryExpression.Type type = unaryExpression.getType();

      if (type.equals(ASTUnaryExpression.Type.NOT)) {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.BOOLEAN)) {
          exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "unary operator ! requires a boolean"));
        }
      } else /* if (type.equals(ASTUnaryExpression.Type.NEGATE)) */ {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.INTEGER)) {
          exceptions.add(new SemanticException(SemanticException.Type.TYPE_MISMATCH, "unary operator - requires an integer"));
        }
      }
    }

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTLocationExpression locationExpression) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTMethodCallExpression methodCallExpression) {
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

  // Noah
  public List<SemanticException> visit(ASTIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  public List<SemanticException> visit(ASTBooleanLiteral booleanLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();
    
    // NOTE(rbd): Nothing to check.

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
