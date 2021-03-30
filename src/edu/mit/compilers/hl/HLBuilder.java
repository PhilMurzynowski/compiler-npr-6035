package edu.mit.compilers.hl;

import java.util.List;
import java.util.Optional;

import edu.mit.compilers.common.*;
import edu.mit.compilers.ast.*;

public class HLBuilder {

  public static HLProgram buildProgram(ASTProgram program) {
    final HLProgram.Builder builder = new HLProgram.Builder();

    final HLSymbolTable symbolTable = new HLSymbolTable();

    for (ASTImportDeclaration astImportDeclaration : program.getImportDeclarations()) {
      final HLImportDeclaration hlImportDeclaration = HLBuilder.buildImportDeclaration(astImportDeclaration);

      symbolTable.addImport(astImportDeclaration.getIdentifier(), hlImportDeclaration);
      builder.addImport(hlImportDeclaration);
    }

    for (ASTFieldDeclaration astFieldDeclaration : program.getFieldDeclarations()) {
      for (ASTFieldDeclaration.Identifier identifier : astFieldDeclaration.getIdentifiers()) {
        if (identifier.getLength().isPresent()) {
          final HLGlobalArrayFieldDeclaration arrayDeclaration = HLBuilder.buildGlobalArrayFieldDeclaration(astFieldDeclaration, identifier);

          symbolTable.addArray(identifier.getIdentifier(), arrayDeclaration);
          builder.addArray(arrayDeclaration);
        } else {
          final HLGlobalScalarFieldDeclaration scalarDeclaration = HLBuilder.buildGlobalScalarFieldDeclaration(astFieldDeclaration, identifier);

          symbolTable.addScalar(identifier.getIdentifier(), scalarDeclaration);
          builder.addScalar(scalarDeclaration);
        }
      }
    }

    for (ASTMethodDeclaration astMethodDeclaration : program.getMethodDeclarations()) {
      final HLMethodDeclaration hlMethodDeclaration = HLBuilder.buildMethodDeclaration(symbolTable, astMethodDeclaration);

      symbolTable.addMethod(astMethodDeclaration.getIdentifier(), hlMethodDeclaration);
      builder.addMethod(hlMethodDeclaration);
    }

    // NOTE(rbd): Kiiind of a hack, but I didn't want to pass HLProgram.Builder down to everyone...
    builder.addStrings(symbolTable.getStringLiterals());

    return builder.build();
  }

  // TODO: Phil
  public static HLImportDeclaration buildImportDeclaration(ASTImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static HLGlobalScalarFieldDeclaration buildGlobalScalarFieldDeclaration(ASTFieldDeclaration fieldDeclaration, ASTFieldDeclaration.Identifier identifier) {
    VariableType type = fieldDeclaration.getType();
    return new HLGlobalScalarFieldDeclaration(type, identifier.getIdentifier());
  }

  // TODO: Noah
  public static HLGlobalArrayFieldDeclaration buildGlobalArrayFieldDeclaration(ASTFieldDeclaration fieldDeclaration, ASTFieldDeclaration.Identifier identifier) {
    throw new RuntimeException("not implemented");
  }

  public static HLMethodDeclaration buildMethodDeclaration(HLSymbolTable symbolTable, ASTMethodDeclaration methodDeclaration) {
    final String identifier = methodDeclaration.getIdentifier();
    final HLBlock body = HLBuilder.buildBlock(symbolTable, methodDeclaration.getBlock(), methodDeclaration.getArguments());
    return new HLMethodDeclaration(identifier, body);
  }

  // DONE: Robert
  public static HLArgumentDeclaration buildArgumentDeclaration(ASTMethodDeclaration.Argument argumentDeclaration, int index) {
    return new HLArgumentDeclaration(argumentDeclaration.getType(), index);
  }

  // TODO: Phil
  public static HLLocalScalarFieldDeclaration buildLocalScalarFieldDeclaration(ASTFieldDeclaration fieldDeclaration, ASTFieldDeclaration.Identifier identifier, int index) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLLocalArrayFieldDeclaration buildLocalArrayFieldDeclaration(ASTFieldDeclaration fieldDeclaration, ASTFieldDeclaration.Identifier identifier, int index) {
    throw new RuntimeException("not implemented");
  }

  public static HLBlock buildBlock(HLSymbolTable symbolTable, ASTBlock block, List<ASTMethodDeclaration.Argument> astArguments) {
    final HLBlock.Builder builder = new HLBlock.Builder();
    symbolTable = new HLSymbolTable(symbolTable);

    for (ASTMethodDeclaration.Argument astArgument : astArguments) {
      final HLArgumentDeclaration hlArgument = HLBuilder.buildArgumentDeclaration(astArgument, builder.argumentIndex());

      symbolTable.addScalar(astArgument.getIdentifier(), hlArgument);
      builder.addArgument(hlArgument);
    }

    for (ASTFieldDeclaration astFieldDeclaration : block.getFieldDeclarations()) {
      for (ASTFieldDeclaration.Identifier identifier : astFieldDeclaration.getIdentifiers()) {
        if (identifier.getLength().isPresent()) {
          final HLLocalArrayFieldDeclaration arrayDeclaration = HLBuilder.buildLocalArrayFieldDeclaration(astFieldDeclaration, identifier, builder.arrayIndex());

          symbolTable.addArray(identifier.getIdentifier(), arrayDeclaration);
          builder.addArray(arrayDeclaration);
        } else {
          final HLLocalScalarFieldDeclaration scalarDeclaration = HLBuilder.buildLocalScalarFieldDeclaration(astFieldDeclaration, identifier, builder.scalarIndex());

          symbolTable.addScalar(identifier.getIdentifier(), scalarDeclaration);
          builder.addScalar(scalarDeclaration);
        }
      }
    }

    for (ASTStatement astStatement : block.getStatements()) {
      final HLStatement hlStatement = HLBuilder.buildStatement(symbolTable, astStatement);

      builder.addStatement(hlStatement);
    }

    return builder.build();
  }

  public static HLStatement buildStatement(HLSymbolTable symbolTable, ASTStatement statement) {
    if (statement instanceof ASTIDAssignStatement idAssignStatement) {
      return buildIDAssignStatement(symbolTable, idAssignStatement);
    } else if (statement instanceof ASTAssignStatement assignStatement) {
      return buildAssignStatement(symbolTable, assignStatement);
    } else if (statement instanceof ASTCompoundAssignStatement compoundAssignStatement) {
      return buildCompoundAssignStatement(symbolTable, compoundAssignStatement);
    } else if (statement instanceof ASTMethodCallStatement methodCallStatement) {
      return buildMethodCallStatement(symbolTable, methodCallStatement);
    } else if (statement instanceof ASTIfStatement ifStatement) {
      return buildIfStatement(symbolTable, ifStatement);
    } else if (statement instanceof ASTForStatement forStatement) {
      return buildForStatement(symbolTable, forStatement);
    } else if (statement instanceof ASTWhileStatement whileStatement) {
      return buildWhileStatement(symbolTable, whileStatement);
    } else if (statement instanceof ASTReturnStatement returnStatement) {
      return buildReturnStatement(symbolTable, returnStatement);
    } else if (statement instanceof ASTBreakStatement breakStatement) {
      return buildBreakStatement(breakStatement);
    } else if (statement instanceof ASTContinueStatement continueStatement) {
      return buildContinueStatement(continueStatement);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static HLStoreScalarStatement buildIDAssignStatement(HLSymbolTable symbolTable, ASTIDAssignStatement idAssignStatement) {
    final HLScalarFieldDeclaration declaration = symbolTable.getScalar(idAssignStatement.getIdentifier());
    final HLExpression expression = HLBuilder.buildExpression(symbolTable, idAssignStatement.getExpression());
    return new HLStoreScalarStatement(declaration, expression);
  }

  // DONE: Robert
  public static HLStoreStatement buildAssignStatement(HLSymbolTable symbolTable, ASTAssignStatement assignStatement) {
    final ASTLocationExpression location = assignStatement.getLocation();
    if (location.getOffset().isPresent()) {
      final HLArrayFieldDeclaration declaration = symbolTable.getArray(location.getIdentifier());
      final HLExpression index = HLBuilder.buildExpression(symbolTable, location.getOffset().get());
      final HLExpression expression = HLBuilder.buildExpression(symbolTable, assignStatement.getExpression());
      return new HLStoreArrayStatement(declaration, index, expression);
    } else {
      final HLScalarFieldDeclaration declaration = symbolTable.getScalar(location.getIdentifier());
      final HLExpression expression = HLBuilder.buildExpression(symbolTable, assignStatement.getExpression());
      return new HLStoreScalarStatement(declaration, expression);
    }
  }

  // TODO: Phil
  public static HLStoreStatement buildCompoundAssignStatement(HLSymbolTable symbolTable, ASTCompoundAssignStatement compoundAssignStatement) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLCallStatement buildMethodCallStatement(HLSymbolTable symbolTable, ASTMethodCallStatement methodCallStatement) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static HLIfStatement buildIfStatement(HLSymbolTable symbolTable, ASTIfStatement ifStatement) {
    final HLExpression condition = HLBuilder.buildExpression(symbolTable, ifStatement.getCondition());
    final HLBlock body = HLBuilder.buildBlock(symbolTable, ifStatement.getBody(), List.of());
    Optional<HLBlock> other = Optional.empty();
    if (ifStatement.getOther().isPresent()) {
      other = Optional.of(HLBuilder.buildBlock(symbolTable, ifStatement.getBody(), List.of()));
    }
    return new HLIfStatement(condition, body, other);
  }

  // TODO: Phil
  public static HLForStatement buildForStatement(HLSymbolTable symbolTable, ASTForStatement forStatement) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLWhileStatement buildWhileStatement(HLSymbolTable symbolTable, ASTWhileStatement whileStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLReturnStatement buildReturnStatement(HLSymbolTable symbolTable, ASTReturnStatement returnStatement) {
    Optional<ASTExpression> astExpression = returnStatement.getExpression();
    if (astExpression.isPresent()) {
      return new HLReturnStatement(Optional.of(HLBuilder.buildExpression(symbolTable, astExpression.get())));
    } else {
      return new HLReturnStatement(Optional.empty());
    }
  }

  // DONE: Robert
  public static HLBreakStatement buildBreakStatement(ASTBreakStatement breakStatement) {
    return new HLBreakStatement();
  }

  // TODO: Phil
  public static HLContinueStatement buildContinueStatement(ASTContinueStatement continueStatement) {
    throw new RuntimeException("not implemented");
  }

  public static HLArgument buildArgument(HLSymbolTable symbolTable, ASTArgument argument) {
    if (argument instanceof ASTExpression expression) {
      return buildExpression(symbolTable, expression);
    } else if (argument instanceof ASTStringLiteral stringLiteral) {
      return buildStringLiteral(symbolTable, stringLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static HLExpression buildExpression(HLSymbolTable symbolTable, ASTExpression expression) {
    if (expression instanceof ASTBinaryExpression binaryExpression) {
      return HLBuilder.buildBinaryExpression(symbolTable, binaryExpression);
    } else if (expression instanceof ASTUnaryExpression unaryExpression) {
      return HLBuilder.buildUnaryExpression(symbolTable, unaryExpression);
    } else if (expression instanceof ASTLocationExpression locationExpression) {
      return HLBuilder.buildLocationExpression(symbolTable, locationExpression);
    } else if (expression instanceof ASTMethodCallExpression methodCallExpression) {
      return HLBuilder.buildMethodCallExpression(symbolTable, methodCallExpression);
    } else if (expression instanceof ASTLengthExpression lengthExpression) {
      return HLBuilder.buildLengthExpression(symbolTable, lengthExpression);
    } else if (expression instanceof ASTIntegerLiteral integerLiteral) {
      return HLBuilder.buildIntegerLiteral(integerLiteral, /* isNegated */ false);
    } else if (expression instanceof ASTCharacterLiteral characterLiteral) {
      return HLBuilder.buildCharacterLiteral(characterLiteral);
    } else if (expression instanceof ASTBooleanLiteral booleanLiteral) {
      return HLBuilder.buildBooleanLiteral(booleanLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static HLBinaryExpression buildBinaryExpression(HLSymbolTable symbolTable, ASTBinaryExpression binaryExpression) {
    HLExpression left = HLBuilder.buildExpression(symbolTable, binaryExpression.getleft());
    HLExpression right = HLBuilder.buildExpression(symbolTable, binaryExpression.getright());
    BinaryExpressionType type = binaryExpression.getType();
    return new HLBinaryExpression(left, type, right);
  }

  public static HLExpression buildUnaryExpression(HLSymbolTable symbolTable, ASTUnaryExpression unaryExpression) {
    if (unaryExpression.getExpression() instanceof ASTIntegerLiteral integerLiteral) {
      assert unaryExpression.getType().equals(UnaryExpressionType.NEGATE);
      return HLBuilder.buildIntegerLiteral(integerLiteral, /* isNegated */ true);
    } else {
      return new HLUnaryExpression(unaryExpression.getType(), HLBuilder.buildExpression(symbolTable, unaryExpression.getExpression()));
    }
  }

  public static HLLoadExpression buildLocationExpression(HLSymbolTable symbolTable, ASTLocationExpression locationExpression) {
    // no offset => load scalar
    if (locationExpression.getOffset().isEmpty()) {
      final HLScalarFieldDeclaration declaration = symbolTable.getScalar(locationExpression.getIdentifier());
      return new HLLoadScalarExpression(declaration);
    // offset => load array
    } else {
      final HLArrayFieldDeclaration declaration = symbolTable.getArray(locationExpression.getIdentifier());
      final HLExpression offset = HLBuilder.buildExpression(symbolTable, locationExpression.getOffset().get());
      return new HLLoadArrayExpression(declaration, offset);
    }
  }

  // TODO: Noah
  public static HLCallExpression buildMethodCallExpression(HLSymbolTable symbolTable, ASTMethodCallExpression methodCallExpression) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static HLLengthExpression buildLengthExpression(HLSymbolTable symbolTable, ASTLengthExpression lengthExpression) {
    final HLArrayFieldDeclaration declaration = symbolTable.getArray(lengthExpression.getIdentifier());
    return new HLLengthExpression(declaration);
  }

  public static HLIntegerLiteral buildIntegerLiteral(ASTIntegerLiteral integerLiteral, boolean isNegated) {
    if (isNegated) {
      return new HLIntegerLiteral(integerLiteral.getValue().negate().longValue());
    } else {
      return new HLIntegerLiteral(integerLiteral.getValue().longValue());
    }
  }

  // TODO: Phil
  public static HLIntegerLiteral buildCharacterLiteral(ASTCharacterLiteral characterLiteral) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static HLIntegerLiteral buildBooleanLiteral(ASTBooleanLiteral booleanLiteral) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static HLStringLiteral buildStringLiteral(HLSymbolTable symbolTable, ASTStringLiteral stringLiteral) {
    HLStringLiteralDeclaration declaration;
    if (!symbolTable.stringLiteralExists(stringLiteral.getValue())) {
      declaration = new HLStringLiteralDeclaration(stringLiteral.getValue());
      symbolTable.addStringLiteral(stringLiteral.getValue(), declaration);
    } else {
      declaration = symbolTable.getStringLiteral(stringLiteral.getValue());
    }
    return new HLStringLiteral(declaration);
  }

}
