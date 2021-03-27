package edu.mit.compilers.sem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import edu.mit.compilers.ast.*;
import edu.mit.compilers.common.*;

public class ProgramChecker implements ASTNode.Visitor<List<SemanticException>> {

  private final SymbolTable symbolTable;
  private final boolean inLoop;
  private final Optional<MethodType> returnType; private final List<ASTMethodDeclaration.Argument> arguments;
  private final boolean inStatement;
  private final boolean isNegated;

  public ProgramChecker(SymbolTable symbolTable, boolean inLoop, Optional<MethodType> returnType, List<ASTMethodDeclaration.Argument> arguments, boolean inStatement, boolean isNegated) {
    this.symbolTable = symbolTable;
    this.inLoop = inLoop;
    this.returnType = returnType;
    this.arguments = arguments;
    this.inStatement = inStatement;
    this.isNegated = isNegated;
  }

  /*
   * Recursively check program import, field, and method declarations.
   * Check for main declaration.
   */
  public List<SemanticException> visit(ASTProgram program) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // not creating symbol table
    for (ASTImportDeclaration importDeclaration : program.getImportDeclarations()) {
      exceptions.addAll(importDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));
    }
    for (ASTFieldDeclaration fieldDeclaration : program.getFieldDeclarations()) {
      exceptions.addAll(fieldDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));
    }
    boolean hasValidMainDeclaration = false;
    for (ASTMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      exceptions.addAll(methodDeclaration.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));
      // check for valid main function
      if (methodDeclaration.getIdentifier().equals("main")
       && methodDeclaration.getMethodType() == MethodType.VOID
       && methodDeclaration.getArguments().isEmpty()) {
          hasValidMainDeclaration = true; 
      }
    }
    if (!hasValidMainDeclaration) { 
      exceptions.add(new SemanticException(program.getTextLocation(), SemanticException.Type.UNDEFINED_MAIN, "missing declaration of main method in program"));
    }
    return exceptions;
  }

  /*
   * Check import does not already exist.
   */
  public List<SemanticException> visit(ASTImportDeclaration importDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = importDeclaration.getIdentifier();

    if (symbolTable.exists(identifier)) {
      exceptions.add(new SemanticException(importDeclaration.getTextLocation(), SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
    } else {
      symbolTable.addImport(identifier);
    }

    return exceptions;
  }

  /*
   * Check for field duplicates, check nonzero length.
   */
  public List<SemanticException> visit(ASTFieldDeclaration fieldDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final VariableType type = fieldDeclaration.getType();
    for (ASTFieldDeclaration.Identifier identifier : fieldDeclaration.getIdentifiers()) {
      // duplicate symbol check
      if (symbolTable.exists(identifier.getIdentifier())) {
        exceptions.add(new SemanticException(identifier.getTextLocation(), SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier.getIdentifier()));
      } else {
        final Optional<ASTIntegerLiteral> length = identifier.getLength();
        // check array index and add to array symbols
        if (length.isPresent()) {
          if (length.get().isZero()) {
            exceptions.add(new SemanticException(length.get().getTextLocation(), SemanticException.Type.INVALID_ARRAY, "array size cannot be 0"));
          }
          final List<SemanticException> lengthExceptions = length.get().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
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

  /*
   * Check for duplicates and recurisively check block.
   */
  public List<SemanticException> visit(ASTMethodDeclaration methodDeclaration) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = methodDeclaration.getIdentifier();
    final ASTBlock block = methodDeclaration.getBlock();
    final Optional<MethodType> type = Optional.of(methodDeclaration.getMethodType());

    if (symbolTable.exists(identifier)) {
      exceptions.add(new SemanticException(methodDeclaration.getTextLocation(), SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
    } else {
      symbolTable.addMethod(identifier, methodDeclaration.getMethodType(), methodDeclaration.getArgumentTypes());
    }
    exceptions.addAll(block.accept(new ProgramChecker(symbolTable, inLoop, type, methodDeclaration.getArguments(), false, false))); // pass in new type and arguments

    return exceptions;
  }

  /*
   * Check if local variable names are shadowing formal parameter names.
   * Recursively check field declarations and statements.
   */
  public List<SemanticException> visit(ASTBlock block) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final SymbolTable blockSymbolTable = new SymbolTable(symbolTable);

    for (ASTMethodDeclaration.Argument argument : arguments) {
      final String identifier = argument.getIdentifier();
      if (blockSymbolTable.exists(identifier)) {
        exceptions.add(new SemanticException(argument.getTextLocation(), SemanticException.Type.DUPLICATE_IDENTIFIER, "duplicate identifier " + identifier));
      }
      blockSymbolTable.addScalar(identifier, argument.getType());
    }

    for (ASTFieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
      exceptions.addAll(fieldDeclaration.accept(new ProgramChecker(blockSymbolTable, inLoop, returnType, List.of(), false, false)));
    }

    for (ASTStatement statement : block.getStatements()) {
      exceptions.addAll(statement.accept(new ProgramChecker(blockSymbolTable, inLoop, returnType, List.of(), false, false)));
    }

    return exceptions;
  }

  /*
   * ASTIDAssignStatment used in the context of a for loop.
   * Check if symbol exists, check expression, and check type match of previous two.
   */
  public List<SemanticException> visit(ASTIDAssignStatement idAssignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    Optional<VariableType> identifierType = Optional.empty();
    Optional<VariableType> expressionType = Optional.empty();

    // scalar identifier exists
    final String identifier = idAssignStatement.getIdentifier();
    if (symbolTable.scalarExists(identifier)) {
      identifierType = Optional.of(symbolTable.scalarType(identifier));
    } else {
      exceptions.add(new SemanticException(idAssignStatement.getTextLocation(), SemanticException.Type.UNDEFINED_IDENTIFIER, "invalid scalar identifier " + identifier));
    }

    // expression is valid
    final ASTExpression expression = idAssignStatement.getExpression();
    final List<SemanticException> exprExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(exprExceptions);
    if (exprExceptions.isEmpty()) {
      expressionType = Optional.of(expression.accept(new ExpressionChecker(symbolTable)));
    }

    // identifier type matches expression type
    if (
        identifierType.isPresent() && expressionType.isPresent()
        && !(identifierType.get().equals(expressionType.get()))
    ) {
      exceptions.add(new SemanticException(idAssignStatement.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "assign statement requires same type for location and evaluated expression"));
    }

    return exceptions;
  }

  /*
   * Check location and expression, and check matching types.
   */
  public List<SemanticException> visit(ASTAssignStatement assignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    ASTLocationExpression location = assignStatement.getLocation();
    ASTExpression expression = assignStatement.getExpression();

    final List<SemanticException> locationExceptions = location.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(locationExceptions);
    exceptions.addAll(expressionExceptions);

    if (locationExceptions.isEmpty() && expressionExceptions.isEmpty()) {
      final VariableType locationType = location.accept(new ExpressionChecker(symbolTable));
      final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));
        if (!expressionType.equals(locationType)) {
          exceptions.add(new SemanticException(assignStatement.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "assign statement requries same type for location and evaluated expression"));
        }
    }

    return exceptions;
  }

  /*
   * Check location and integer types of left and right hand sides.
   */
  public List<SemanticException> visit(ASTCompoundAssignStatement compoundAssignStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTLocationExpression location = compoundAssignStatement.getLocation();

    final List<SemanticException> locationExceptions = location.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(locationExceptions);

    if (locationExceptions.isEmpty()) {
      final VariableType locationType = location.accept(new ExpressionChecker(symbolTable));

      if (!locationType.equals(VariableType.INTEGER)) {
        exceptions.add(new SemanticException(location.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "left side of compound assign statment requires an integer"));
      }
    }

    if (compoundAssignStatement.getExpression().isPresent()) {
      final ASTExpression expression = compoundAssignStatement.getExpression().get();

      final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
      exceptions.addAll(expressionExceptions);

      if (expressionExceptions.isEmpty()) {
        final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

        if (!expressionType.equals(VariableType.INTEGER)) {
          exceptions.add(new SemanticException(expression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "right side of compound assign statement requires an integer"));
        }
      }
    }

    return exceptions;
  }

  /*
   * Relies completely on ASTMethodCallExpression.
   */
  public List<SemanticException> visit(ASTMethodCallStatement methodCallStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // relies completely on ASTMethodCallExpression
    exceptions.addAll(methodCallStatement.getCall().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), true, false)));

    return exceptions;
  }

  public List<SemanticException> visit(ASTIfStatement ifStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression condition = ifStatement.getCondition();
    final ASTBlock body = ifStatement.getBody();
    final Optional<ASTBlock> other = ifStatement.getOther();

    final List<SemanticException> conditionExceptions = condition.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(conditionExceptions);

    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));

      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(condition.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "if condition expression requires a boolean"));
      }
    }

    exceptions.addAll(body.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));
    if (other.isPresent()) {
      exceptions.addAll(other.get().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));
    }

    return exceptions;
  }

  /*
   * Check the id assign statment, condition to be a valid boolean expr, and compound assign update.
   */
  public List<SemanticException> visit(ASTForStatement forStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    exceptions.addAll(forStatement.getInitial().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));

    final ASTExpression condition = forStatement.getCondition();

    final List<SemanticException> conditionExceptions = condition.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(conditionExceptions);

    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));

      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(condition.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "for loop condition expression requires a boolean"));
      }
    }

    exceptions.addAll(forStatement.getUpdate().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false)));

    exceptions.addAll(forStatement.getBody().accept(new ProgramChecker(symbolTable, true, returnType, List.of(), false, false)));

    return exceptions;
  }

  /*
   * Check condition and recursively check body.
   */
  public List<SemanticException> visit(ASTWhileStatement whileStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // verify condition expression is valid and evaluates to boolean
    final ASTExpression condition = whileStatement.getCondition();
    final List<SemanticException> conditionExceptions =  condition.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(conditionExceptions);
    if (conditionExceptions.isEmpty()) {
      final VariableType conditionType = condition.accept(new ExpressionChecker(symbolTable));
      if (!conditionType.equals(VariableType.BOOLEAN)) {
        exceptions.add(new SemanticException(condition.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "while loop condition expression requires a boolean"));
      }
    }

    // collect semantic errors from body
    final ASTBlock body = whileStatement.getBody();
    exceptions.addAll(body.accept(new ProgramChecker(symbolTable, true, returnType, List.of(), false, false)));

    return exceptions;
  }

  /*
   * Check expression if exists, then check return keyword is within a method, and that method return type matches return statement.
   */
  public List<SemanticException> visit(ASTReturnStatement returnStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();
    Optional<ASTExpression> expression = returnStatement.getExpression();

    List<SemanticException> expressionExceptions = new ArrayList<SemanticException>();

    if (expression.isPresent()) {
      expressionExceptions = expression.get().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
      exceptions.addAll(expressionExceptions);
    }

    if (!returnType.isPresent()) {
      exceptions.add(new SemanticException(returnStatement.getTextLocation(), SemanticException.Type.INVALID_KEYWORD, "return keyword not allowed outside of a method declaration"));
    } else {

      if (!expression.isPresent() && returnType.get() != MethodType.VOID) {
        exceptions.add(new SemanticException(returnStatement.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "must return expression for method not of type void"));

      } else if (expression.isPresent() && expressionExceptions.isEmpty()) {
        final VariableType expressionType = expression.get().accept(new ExpressionChecker(symbolTable));
        if (!expressionType.toMethodType().equals(returnType.get())) {
          exceptions.add(new SemanticException(expression.get().getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "type of returned expression must match method return type, must not return expression for method of type void"));
        }
      }
    }
    

    return exceptions;
  }

  /*
   * Check if within a loop.
   */
  public List<SemanticException> visit(ASTBreakStatement breakStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException(breakStatement.getTextLocation(), SemanticException.Type.INVALID_KEYWORD, "break keyword not allowed outside of a loop"));
    }

    return exceptions;
  }

  /*
   * Check if within a loop.
   */
  public List<SemanticException> visit(ASTContinueStatement continueStatement) {
    final List<SemanticException> exceptions = new ArrayList<>();

    if (!inLoop) {
      exceptions.add(new SemanticException(continueStatement.getTextLocation(), SemanticException.Type.INVALID_KEYWORD, "continue keyword not allowed outside of a loop"));
    }

    return exceptions;
  }

  /*
   * Check l and rhs, check types match.
   */
  public List<SemanticException> visit(ASTBinaryExpression binaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression left = binaryExpression.getleft();
    final ASTExpression right = binaryExpression.getright();

    final List<SemanticException> leftExceptions = left.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    final List<SemanticException> rightExceptions = right.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
    exceptions.addAll(leftExceptions);
    exceptions.addAll(rightExceptions);

    if (leftExceptions.isEmpty() && rightExceptions.isEmpty()) {
      
      final VariableType leftType = left.accept(new ExpressionChecker(symbolTable));
      final VariableType rightType = right.accept(new ExpressionChecker(symbolTable));

      if (!leftType.equals(rightType)) {
        exceptions.add(new SemanticException(binaryExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "left and right side of binary operator must have same type"));
      } else if (!binaryExpression.acceptsType(leftType)) {
        exceptions.add(new SemanticException(binaryExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "incorrect operand types for binary operator"));
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTUnaryExpression unaryExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final ASTExpression expression = unaryExpression.getExpression();

    final List<SemanticException> expressionExceptions = expression.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, unaryExpression.getType().equals(ASTUnaryExpression.Type.NEGATE)));
    exceptions.addAll(expressionExceptions);

    if (expressionExceptions.isEmpty()) {
      final VariableType expressionType = expression.accept(new ExpressionChecker(symbolTable));

      if (!unaryExpression.acceptsType(expressionType)) {
        exceptions.add(new SemanticException(expression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "incorrect operand type for unary operator"));
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTLocationExpression locationExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String locationId = locationExpression.getIdentifier();
    // location is scalar variable
    if (symbolTable.scalarExists(locationId)) {
      if (locationExpression.getOffset().isPresent()) {
        exceptions.add(new SemanticException(locationExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "cannot index a scalar"));
      }
      return exceptions;
    // location is array variable
    } else if (symbolTable.arrayExists(locationId)) {
      // array has offset
      final Optional<ASTExpression> offset = locationExpression.getOffset();
      if (offset.isPresent()) {
        // offset is semantically valid
        final List<SemanticException> offsetExceptions = offset.get().accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
        exceptions.addAll(offsetExceptions);
        if (offsetExceptions.isEmpty()) {
          // offset evaluates to integer
          final VariableType offsetType = offset.get().accept(new ExpressionChecker(symbolTable));
          if (!offsetType.equals(VariableType.INTEGER)) {
            exceptions.add(new SemanticException(offset.get().getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "array index must evaluate to an integer"));
          }
        }
      } else {
        exceptions.add(new SemanticException(locationExpression.getTextLocation(), SemanticException.Type.MISSING_SYMBOL, "array must have index"));
      }

    } else if (symbolTable.exists(locationId)) {
      exceptions.add(new SemanticException(locationExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "invalid location: " + locationId));
    } else {
      exceptions.add(new SemanticException(locationExpression.getTextLocation(), SemanticException.Type.UNDEFINED_IDENTIFIER, "undefined identifier " + locationId));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTMethodCallExpression methodCallExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = methodCallExpression.getIdentifier();

    if (!symbolTable.methodExists(identifier) && !symbolTable.importExists(identifier)) {
      exceptions.add(new SemanticException(methodCallExpression.getTextLocation(), SemanticException.Type.UNDEFINED_IDENTIFIER, "undefined identifier " + identifier));
    }

    // NOTE: should not have any shadowing methods
    if (symbolTable.methodExists(identifier)) {

      // Cannot have a method with returnType void
      // NOTE(rbd): yeah, we can, if it is used in a MethodCallStatement. this case is covered by the ExpressionChecker
      if (!inStatement && symbolTable.methodReturnType(identifier).equals(MethodType.VOID)) {
        exceptions.add(new SemanticException(methodCallExpression.getTextLocation(), SemanticException.Type.TYPE_MISMATCH, "method of return type void in expression"));
      }

      List<VariableType> methodDeclarationArguments = symbolTable.methodArgumentTypes(identifier); 
      List<ASTArgument> methodCallArguments = methodCallExpression.getArguments();
      List<SemanticException> argumentExceptions = new ArrayList<>();
      // Arguments after they have been converted to ASTExpressions
      List<ASTExpression> methodCallExpressions = new ArrayList<>();

      for (ASTArgument callArgument : methodCallArguments) {
        List<SemanticException> callArgumentExceptions = callArgument.accept(new ProgramChecker(symbolTable, inLoop, returnType, List.of(), false, false));
        argumentExceptions.addAll(callArgumentExceptions);

        if (callArgumentExceptions.isEmpty()) {
          Either<ASTExpression, SemanticException> either = callArgument.accept(new ArgumentChecker(symbolTable));

          if (either.isRight()) {
            argumentExceptions.add(either.right());

          } else {
            methodCallExpressions.add(either.left());
          }
        }
      }

      exceptions.addAll(argumentExceptions);

      if (argumentExceptions.isEmpty()) {
        final int decl_size = methodDeclarationArguments.size();
        final int call_size = methodCallExpressions.size();

        if (decl_size != call_size) {
          exceptions.add(new SemanticException(methodCallExpression.getTextLocation(), SemanticException.Type.INCOMPATIBLE_ARGUMENTS, "incorrect number of arguments, expected "+decl_size+" got "+call_size));
        } else {

          for (int i=0; i < decl_size; i++) {
            VariableType callArgumentType = methodCallExpressions.get(i).accept(new ExpressionChecker(symbolTable));
            VariableType declaredArgumentType = methodDeclarationArguments.get(i);

            if (!declaredArgumentType.equals(callArgumentType)) {
              exceptions.add(new SemanticException(methodCallExpressions.get(i).getTextLocation(), SemanticException.Type.INCOMPATIBLE_ARGUMENTS, ""+i+"(th/st) argument of incorrect type, expected " + declaredArgumentType.name()+" got "+callArgumentType.name()));
            }

          }
        }
      }
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTLengthExpression lengthExpression) {
    final List<SemanticException> exceptions = new ArrayList<>();

    final String identifier = lengthExpression.getIdentifier();

    if (!symbolTable.arrayExists(identifier)) {
      exceptions.add(new SemanticException(lengthExpression.getTextLocation(), SemanticException.Type.UNDEFINED_IDENTIFIER, "undefined identifier " + identifier));
    }

    return exceptions;
  }

  public List<SemanticException> visit(ASTIntegerLiteral integerLiteral) {
    final List<SemanticException> exceptions = new ArrayList<>();

    // if negated and Long.MIN_VALUE > -(integerLiteral)
    if (isNegated && BigInteger.valueOf(Long.MIN_VALUE).compareTo(integerLiteral.getValue().negate()) > 0) {
      exceptions.add(new SemanticException(integerLiteral.getTextLocation(), SemanticException.Type.OUT_OF_RANGE, "negative integer -" + integerLiteral.getValue().toString() + " is out of range"));
    // if Long.MAX_VALUE < -(integerLiteral)
    } else if ((!isNegated) && BigInteger.valueOf(Long.MAX_VALUE).compareTo(integerLiteral.getValue()) < 0) {
      exceptions.add(new SemanticException(integerLiteral.getTextLocation(), SemanticException.Type.OUT_OF_RANGE, "positive integer " + integerLiteral.getValue().toString() + " is out of range"));
    }

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
