package edu.mit.compilers.ir;

import java.util.List;
import java.util.ArrayList;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

public class ProgramChecker implements ASTNode.Visitor<List<SemanticException>> {

  private final SymbolTable symbolTable;
  private final boolean inLoop;

  // Noah
  public ProgramChecker(SymbolTable symbolTable, boolean inLoop) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTProgram program) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTImportDeclaration importDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = importDeclaration.getIdentifier();

    if (symbolTable.exists(identifier)) {
      exceptions.add(new SemanticException());
    } else {
      symbolTable.addImport(identifier);
    }

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTFieldDeclaration fieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTBlock block) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final SymbolTable blockSymbolTable = new SymbolTable(symbolTable);

    for (ASTFieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
      exceptions.addAll(fieldDeclaration.accept(new ProgramChecker(blockSymbolTable, inLoop)));
    }

    for (ASTStatement statement : block.getStatements()) {
      exceptions.addAll(statement.accept(new ProgramChecker(blockSymbolTable, inLoop)));
    }

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTIDAssignStatement idAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public List<SemanticException> visit(ASTAssignStatement assignStatement) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public List<SemanticException> visit(ASTCompoundAssignStatement compoundAssignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTLocationExpression location = compoundAssignStatement.getLocation();

    final List<SemanticException> locationExceptions = location.accept(new ProgramChecker(symbolTable, inLoop));
    exceptions.addAll(locationExceptions);

    if (locationExceptions.isEmpty()) {
      final VariableType locationType = location.accept(new ExpressionChecker(symbolTable));

      if (!locationType.equals(VariableType.INTEGER)) {
        exceptions.add(new SemanticException());
      }
    }

    if (compoundAssignStatement.getExpression().isPresent()) {
      final ASTExpression expression = compoundAssignStatement.getExpression().get();

      final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop));
      exceptions.addAll(expressionExceptions);

      if (expressionExceptions.isEmpty()) {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.INTEGER)) {
          exceptions.add(new SemanticException());
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

  // Robert
  public List<SemanticException> visit(ASTForStatement forStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    exceptions.addAll(forStatement.getInitial().accept(new ProgramChecker(symbolTable, inLoop)));

    exceptions.addAll(forStatement.getCondition().accept(new ProgramChecker(symbolTable, inLoop)));

    exceptions.addAll(forStatement.getUpdate().accept(new ProgramChecker(symbolTable, inLoop)));

    exceptions.addAll(forStatement.getBody().accept(new ProgramChecker(symbolTable, true)));

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

  // Robert
  public List<SemanticException> visit(ASTBreakStatement breakStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException());
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

  // Robert
  public List<SemanticException> visit(ASTUnaryExpression unaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression expression = unaryExpression.getExpression();

    final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop));
    exceptions.addAll(expressionExceptions);

    if (expressionExceptions.isEmpty()) {
      final ASTUnaryExpression.Type type = unaryExpression.getType();

      if (type.equals(ASTUnaryExpression.Type.NOT)) {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.BOOLEAN)) {
          exceptions.add(new SemanticException());
        }
      } else /* if (type.equals(ASTUnaryExpression.Type.NEGATE)) */ {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.INTEGER)) {
          exceptions.add(new SemanticException());
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

  // Robert
  public List<SemanticException> visit(ASTLengthExpression lengthExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!symbolTable.arrayExists(lengthExpression.getIdentifier())) {
      exceptions.add(new SemanticException());
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

  // Robert
  public List<SemanticException> visit(ASTBooleanLiteral booleanLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();

    return exceptions;
  }

  // Noah
  public List<SemanticException> visit(ASTStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
