package edu.mit.compilers.ast;

import java.util.List;

import edu.mit.compilers.common.*;
import edu.mit.compilers.tk.*;
import edu.mit.compilers.pt.*;

public class Abstracter {

  // Program -> ImportDeclaration* FieldDeclaration* MethodDeclaration*
  public ASTProgram abstractProgram(PTNode ptProgram) {
    assert ptProgram.is(PTNonterminal.Type.PROGRAM);

    final ASTProgram.Builder builder = new ASTProgram.Builder();
    final Peekable nodes = Peekable.of(ptProgram.getChildren());

    while (nodes.peek().is(PTNonterminal.Type.IMPORT_DECLARATION)) {
      builder.addImportDeclaration(abstractImportDeclaration(nodes.next()));
    }

    while (nodes.peek().is(PTNonterminal.Type.FIELD_DECLARATION)) {
      builder.addFieldDeclaration(abstractFieldDeclaration(nodes.next()));
    }

    while (nodes.peek().is(PTNonterminal.Type.METHOD_DECLARATION)) {
      builder.addMethodDeclaration(abstractMethodDeclaration(nodes.next()));
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // ImportDeclaration -> IMPORT IDENTIFIER SEMICOLON
  private ASTImportDeclaration abstractImportDeclaration(PTNode ptImportDeclaration) {
    assert ptImportDeclaration.is(PTNonterminal.Type.IMPORT_DECLARATION);

    ASTImportDeclaration importDeclaration;
    final Peekable nodes = Peekable.of(ptImportDeclaration.getChildren());

    assert nodes.peek().is(Token.Type.IMPORT);
    nodes.next();

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    importDeclaration = new ASTImportDeclaration(ptImportDeclaration.getTextLocation(), nodes.next().getText());

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return importDeclaration;
  }

  // FieldDeclaration -> (INT | BOOL) FieldIdentifierDeclaration (COMMA FieldIdentifierDeclaration)* SEMICOLON
  private ASTFieldDeclaration abstractFieldDeclaration(PTNode ptFieldDeclaration) {
    assert ptFieldDeclaration.is(PTNonterminal.Type.FIELD_DECLARATION);

    final ASTFieldDeclaration.Builder builder = new ASTFieldDeclaration.Builder(ptFieldDeclaration.getTextLocation());
    final Peekable nodes = Peekable.of(ptFieldDeclaration.getChildren());

    if (nodes.peek().is(Token.Type.INT)) {
      builder.withType(VariableType.INTEGER);
    } else if (nodes.peek().is(Token.Type.BOOL)) {
      builder.withType(VariableType.BOOLEAN);
    } else {
      throw new RuntimeException("unreachable");
    }
    nodes.next();

    builder.addIdentifier(abstractFieldIdentifierDeclaration(nodes.next()));

    while (nodes.peek().is(Token.Type.COMMA)) {
      nodes.next();

      builder.addIdentifier(abstractFieldIdentifierDeclaration(nodes.next()));
    }

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return builder.build();
  }

  // MethodDeclaration -> (INT | BOOL | VOID) IDENTIFIER LEFT_ROUND (ArgumentDeclaration (COMMA ArgumentDeclaration)*)? RIGHT_ROUND Block
  private ASTMethodDeclaration abstractMethodDeclaration(PTNode ptMethodDeclaration) {
    assert ptMethodDeclaration.is(PTNonterminal.Type.METHOD_DECLARATION);

    final ASTMethodDeclaration.Builder builder = new ASTMethodDeclaration.Builder(ptMethodDeclaration.getTextLocation());
    final Peekable nodes = Peekable.of(ptMethodDeclaration.getChildren());

    if (nodes.peek().is(Token.Type.INT)) {
      builder.withType(MethodType.INTEGER);
    } else if (nodes.peek().is(Token.Type.BOOL)) {
      builder.withType(MethodType.BOOLEAN);
    } else if (nodes.peek().is(Token.Type.VOID)) {
      builder.withType(MethodType.VOID);
    } else {
      throw new RuntimeException("unreachable");
    }
    nodes.next();

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    if (nodes.peek().is(PTNonterminal.Type.ARGUMENT_DECLARATION)) {
      builder.addArgument(abstractArgumentDeclaration(nodes.next()));

      while (nodes.peek().is(Token.Type.COMMA)) {
        nodes.next();

        builder.addArgument(abstractArgumentDeclaration(nodes.next()));
      }
    }

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    builder.withBlock(abstractBlock(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // FieldIdentifierDeclaration -> IDENTIFIER (LEFT_SQUARE IntegerLiteral RIGHT_SQUARE)?
  private ASTFieldDeclaration.Identifier abstractFieldIdentifierDeclaration(PTNode ptFieldIdentifierDeclaration) {
    assert ptFieldIdentifierDeclaration.is(PTNonterminal.Type.FIELD_IDENTIFIER_DECLARATION);

    final ASTFieldDeclaration.Identifier.Builder builder = new ASTFieldDeclaration.Identifier.Builder(ptFieldIdentifierDeclaration.getTextLocation());
    final Peekable nodes = Peekable.of(ptFieldIdentifierDeclaration.getChildren());

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    if (nodes.peek().is(Token.Type.LEFT_SQUARE)) {
      nodes.next();

      builder.withLength(abstractIntegerLiteral(nodes.next()));

      assert nodes.peek().is(Token.Type.RIGHT_SQUARE);
      nodes.next();
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // ArgumentDeclaration -> (INT | BOOL) IDENTIFIER
  private ASTMethodDeclaration.Argument abstractArgumentDeclaration(PTNode ptArgumentDeclaration) {
    assert ptArgumentDeclaration.is(PTNonterminal.Type.ARGUMENT_DECLARATION);

    final ASTMethodDeclaration.Argument.Builder builder = new ASTMethodDeclaration.Argument.Builder(ptArgumentDeclaration.getTextLocation());
    final Peekable nodes = Peekable.of(ptArgumentDeclaration.getChildren());

    if (nodes.peek().is(Token.Type.INT)) {
      builder.withType(VariableType.INTEGER);
    } else if (nodes.peek().is(Token.Type.BOOL)) {
      builder.withType(VariableType.BOOLEAN);
    } else {
      throw new RuntimeException("unreachable");
    }
    nodes.next();

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    assert !nodes.hasNext();

    return builder.build();
  }

  // Block -> LEFT_CURLY FieldDeclaration* Statement* RIGHT_CURLY
  private ASTBlock abstractBlock(PTNode ptBlock) {
    assert ptBlock.is(PTNonterminal.Type.BLOCK);

    final ASTBlock.Builder builder = new ASTBlock.Builder(ptBlock.getTextLocation());
    final Peekable nodes = Peekable.of(ptBlock.getChildren());

    assert nodes.peek().is(Token.Type.LEFT_CURLY);
    nodes.next();

    while (nodes.peek().is(PTNonterminal.Type.FIELD_DECLARATION)) {
      builder.addFieldDeclaration(abstractFieldDeclaration(nodes.next()));
    }

    while (nodes.peek().is(PTNonterminal.Type.STATEMENT)) {
      builder.addStatement(abstractStatement(nodes.next()));
    }

    assert nodes.peek().is(Token.Type.RIGHT_CURLY);
    nodes.next();

    assert !nodes.hasNext();

    return builder.build();
  }

  // Statement -> IDAssignStatement | AssignStatement | CompoundAssignStatement | MethodCallStatement | IfStatement | ForStatement | WhileStatement | ReturnStatement | BreakStatement | ContinueStatement
  private ASTStatement abstractStatement(PTNode ptStatement) {
    assert ptStatement.is(PTNonterminal.Type.STATEMENT);

    ASTStatement statement;
    final Peekable nodes = Peekable.of(ptStatement.getChildren());

    if (nodes.peek().is(PTNonterminal.Type.ID_ASSIGN_STATEMENT)) {
      statement = abstractIDAssignStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.ASSIGN_STATEMENT)) {
      statement = abstractAssignStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.COMPOUND_ASSIGN_STATEMENT)) {
      statement = abstractCompoundAssignStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.METHOD_CALL_STATEMENT)) {
      statement = abstractMethodCallStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.IF_STATEMENT)) {
      statement = abstractIfStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.FOR_STATEMENT)) {
      statement = abstractForStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.WHILE_STATEMENT)) {
      statement = abstractWhileStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.RETURN_STATEMENT)) {
      statement = abstractReturnStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.BREAK_STATEMENT)) {
      statement = abstractBreakStatement(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.CONTINUE_STATEMENT)) {
      statement = abstractContinueStatement(nodes.next());
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return statement;
  }

  // IDAssignStatement -> IDAssignExpression SEMICOLON
  private ASTIDAssignStatement abstractIDAssignStatement(PTNode ptIDAssignStatement) {
    assert ptIDAssignStatement.is(PTNonterminal.Type.ID_ASSIGN_STATEMENT);

    ASTIDAssignStatement idAssignStatement;
    final Peekable nodes = Peekable.of(ptIDAssignStatement.getChildren());

    idAssignStatement = abstractIDAssignExpression(nodes.next());

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return idAssignStatement;
  }

  // AssignStatement -> LocationExpression EQUAL Expression SEMICOLON
  private ASTAssignStatement abstractAssignStatement(PTNode ptAssignStatement) {
    assert ptAssignStatement.is(PTNonterminal.Type.ASSIGN_STATEMENT);

    final ASTAssignStatement.Builder builder = new ASTAssignStatement.Builder(ptAssignStatement.getTextLocation());
    final Peekable nodes = Peekable.of(ptAssignStatement.getChildren());

    builder.withLocation(abstractLocationExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.EQUAL);
    nodes.next();

    builder.withExpression(abstractExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return builder.build();
  }

  // CompoundAssignStatement -> CompoundAssignExpression SEMICOLON
  private ASTCompoundAssignStatement abstractCompoundAssignStatement(PTNode ptCompoundAssignStatement) {
    assert ptCompoundAssignStatement.is(PTNonterminal.Type.COMPOUND_ASSIGN_STATEMENT);

    ASTCompoundAssignStatement compoundAssignStatement;
    final Peekable nodes = Peekable.of(ptCompoundAssignStatement.getChildren());

    compoundAssignStatement = abstractCompoundAssignExpression(nodes.next());

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return compoundAssignStatement;
  }

  // MethodCallStatement -> MethodCallExpression SEMICOLON
  private ASTMethodCallStatement abstractMethodCallStatement(PTNode ptMethodCallStatement) {
    assert ptMethodCallStatement.is(PTNonterminal.Type.METHOD_CALL_STATEMENT);

    ASTMethodCallStatement methodCallStatement;
    final Peekable nodes = Peekable.of(ptMethodCallStatement.getChildren());

    methodCallStatement = new ASTMethodCallStatement(ptMethodCallStatement.getTextLocation(), abstractMethodCallExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return methodCallStatement;
  }

  // IfStatement -> IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
  private ASTIfStatement abstractIfStatement(PTNode ptIfStatement) {
    assert ptIfStatement.is(PTNonterminal.Type.IF_STATEMENT);

    final ASTIfStatement.Builder builder = new ASTIfStatement.Builder(ptIfStatement.getTextLocation());
    final Peekable nodes = Peekable.of(ptIfStatement.getChildren());

    assert nodes.peek().is(Token.Type.IF);
    nodes.next();

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    builder.withCondition(abstractExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    builder.withBody(abstractBlock(nodes.next()));

    if (nodes.peek().is(Token.Type.ELSE)) {
      nodes.next();

      builder.withOther(abstractBlock(nodes.next()));
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // ForStatement -> FOR LEFT_ROUND AssignExpression SEMICOLON Expression SEMICOLON CompoundAssignExpression RIGHT_ROUND Block
  private ASTForStatement abstractForStatement(PTNode ptForStatement) {
    assert ptForStatement.is(PTNonterminal.Type.FOR_STATEMENT);

    final ASTForStatement.Builder builder = new ASTForStatement.Builder(ptForStatement.getTextLocation());
    final Peekable nodes = Peekable.of(ptForStatement.getChildren());

    assert nodes.peek().is(Token.Type.FOR);
    nodes.next();

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    builder.withInitial(abstractIDAssignExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    builder.withCondition(abstractExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    builder.withUpdate(abstractCompoundAssignExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    builder.withBody(abstractBlock(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // WhileStatement -> WHILE LEFT_ROUND Expression RIGHT_ROUND Block
  private ASTWhileStatement abstractWhileStatement(PTNode ptWhileStatement) {
    assert ptWhileStatement.is(PTNonterminal.Type.WHILE_STATEMENT);

    final ASTWhileStatement.Builder builder = new ASTWhileStatement.Builder(ptWhileStatement.getTextLocation());
    final Peekable nodes = Peekable.of(ptWhileStatement.getChildren());

    assert nodes.peek().is(Token.Type.WHILE);
    nodes.next();

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    builder.withCondition(abstractExpression(nodes.next()));

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    builder.withBody(abstractBlock(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // ReturnStatement -> RETURN (Expression)? SEMICOLON
  private ASTReturnStatement abstractReturnStatement(PTNode ptReturnStatement) {
    assert ptReturnStatement.is(PTNonterminal.Type.RETURN_STATEMENT);

    final ASTReturnStatement.Builder builder = new ASTReturnStatement.Builder(ptReturnStatement.getTextLocation());
    final Peekable nodes = Peekable.of(ptReturnStatement.getChildren());

    assert nodes.peek().is(Token.Type.RETURN);
    nodes.next();

    if (nodes.peek().is(PTNonterminal.Type.EXPRESSION)) {
      builder.withExpression(abstractExpression(nodes.next()));
    }

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return builder.build();
  }

  // BreakStatement -> BREAK SEMICOLON
  private ASTBreakStatement abstractBreakStatement(PTNode ptBreakStatement) {
    assert ptBreakStatement.is(PTNonterminal.Type.BREAK_STATEMENT);

    final Peekable nodes = Peekable.of(ptBreakStatement.getChildren());

    assert nodes.peek().is(Token.Type.BREAK);
    nodes.next();

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return new ASTBreakStatement(ptBreakStatement.getTextLocation());
  }

  // ContinueStatement -> CONTINUE SEMICOLON
  private ASTContinueStatement abstractContinueStatement(PTNode ptContinueStatement) {
    assert ptContinueStatement.is(PTNonterminal.Type.CONTINUE_STATEMENT);

    final Peekable nodes = Peekable.of(ptContinueStatement.getChildren());

    assert nodes.peek().is(Token.Type.CONTINUE);
    nodes.next();

    assert nodes.peek().is(Token.Type.SEMICOLON);
    nodes.next();

    assert !nodes.hasNext();

    return new ASTContinueStatement(ptContinueStatement.getTextLocation());
  }

  // IDAssignExpression -> IDENTIFIER EQUAL Expression
  private ASTIDAssignStatement abstractIDAssignExpression(PTNode ptIDAssignExpression) {
    assert ptIDAssignExpression.is(PTNonterminal.Type.ID_ASSIGN_EXPRESSION);

    final ASTIDAssignStatement.Builder builder = new ASTIDAssignStatement.Builder(ptIDAssignExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptIDAssignExpression.getChildren());

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    assert nodes.peek().is(Token.Type.EQUAL);
    nodes.next();

    builder.withExpression(abstractExpression(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // CompoundAssignExpression -> LocationExpression ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS))
  private ASTCompoundAssignStatement abstractCompoundAssignExpression(PTNode ptCompoundAssignExpression) {
    assert ptCompoundAssignExpression.is(PTNonterminal.Type.COMPOUND_ASSIGN_EXPRESSION);

    final ASTCompoundAssignStatement.Builder builder = new ASTCompoundAssignStatement.Builder(ptCompoundAssignExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptCompoundAssignExpression.getChildren());

    builder.withLocation(abstractLocationExpression(nodes.next()));

    if (nodes.peek().is(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL)) {
      if (nodes.peek().is(Token.Type.PLUS_EQUAL)) {
        builder.withType(ASTCompoundAssignStatement.Type.ADD);
      } else /* if (nodes.peek().is(Token.Type.MINUS_EQUAL)) */ {
        builder.withType(ASTCompoundAssignStatement.Type.SUBTRACT);
      }
      nodes.next();

      builder.withExpression(abstractExpression(nodes.next()));
    } else if (nodes.peek().is(Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
      if (nodes.peek().is(Token.Type.PLUS_PLUS)) {
        builder.withType(ASTCompoundAssignStatement.Type.INCREMENT);
      } else /* if (nodes.peek().is(Token.Type.MINUS_MINUS)) */ {
        builder.withType(ASTCompoundAssignStatement.Type.DECREMENT);
      }
      nodes.next();
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // Expression -> OrExpression
  private ASTExpression abstractExpression(PTNode ptExpression) {
    assert ptExpression.is(PTNonterminal.Type.EXPRESSION);

    ASTExpression expression;
    final Peekable nodes = Peekable.of(ptExpression.getChildren());

    expression = abstractOrExpression(nodes.next());

    assert !nodes.hasNext();

    return expression;
  }

  // OrExpression -> AndExpression (VERTICAL_VERTICAL AndExpression)*
  private ASTExpression abstractOrExpression(PTNode ptOrExpression) {
    assert ptOrExpression.is(PTNonterminal.Type.OR_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptOrExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptOrExpression.getChildren());

    builder.withExpression(abstractAndExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.VERTICAL_VERTICAL)) {
      nodes.next();

      builder.withExpression(ASTBinaryExpression.Type.OR, abstractAndExpression(nodes.next()));
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // AndExpression -> EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
  private ASTExpression abstractAndExpression(PTNode ptAndExpression) {
    assert ptAndExpression.is(PTNonterminal.Type.AND_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptAndExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptAndExpression.getChildren());

    builder.withExpression(abstractEqualityExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.AMPERSAND_AMPERSAND)) {
      nodes.next();

      builder.withExpression(ASTBinaryExpression.Type.AND, abstractEqualityExpression(nodes.next()));
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // EqualityExpression -> RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
  private ASTExpression abstractEqualityExpression(PTNode ptEqualityExpression) {
    assert ptEqualityExpression.is(PTNonterminal.Type.EQUALITY_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptEqualityExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptEqualityExpression.getChildren());

    builder.withExpression(abstractRelationalExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.EQUAL_EQUAL, Token.Type.BANG_EQUAL)) {
      if (nodes.peek().is(Token.Type.EQUAL_EQUAL)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.EQUAL, abstractRelationalExpression(nodes.next()));
      } else /* if (nodes.peek().is(Token.Type.BANG_EQUAL)) */ {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.NOT_EQUAL, abstractRelationalExpression(nodes.next()));
      }
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // RelationalExpression -> AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
  private ASTExpression abstractRelationalExpression(PTNode ptRelationalExpression) {
    assert ptRelationalExpression.is(PTNonterminal.Type.RELATIONAL_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptRelationalExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptRelationalExpression.getChildren());

    builder.withExpression(abstractAdditiveExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.LESS, Token.Type.LESS_EQUAL, Token.Type.GREATER, Token.Type.GREATER_EQUAL)) {
      if (nodes.peek().is(Token.Type.LESS)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.LESS_THAN, abstractAdditiveExpression(nodes.next()));
      } else if (nodes.peek().is(Token.Type.LESS_EQUAL)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.LESS_THAN_OR_EQUAL, abstractAdditiveExpression(nodes.next()));
      } else if (nodes.peek().is(Token.Type.GREATER)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.GREATER_THAN, abstractAdditiveExpression(nodes.next()));
      } else /* if (nodes.peek().is(Token.Type.GREATER_EQUAL)) */ {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.GREATER_THAN_OR_EQUAL, abstractAdditiveExpression(nodes.next()));
      }
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // AdditiveExpression -> MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
  private ASTExpression abstractAdditiveExpression(PTNode ptAdditiveExpression) {
    assert ptAdditiveExpression.is(PTNonterminal.Type.ADDITIVE_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptAdditiveExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptAdditiveExpression.getChildren());

    builder.withExpression(abstractMultiplicativeExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.PLUS, Token.Type.MINUS)) {
      if (nodes.peek().is(Token.Type.PLUS)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.ADD, abstractMultiplicativeExpression(nodes.next()));
      } else /* if (nodes.peek().is(Token.Type.MINUS)) */ {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.SUBTRACT, abstractMultiplicativeExpression(nodes.next()));
      }
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // MultiplicativeExpression -> NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
  private ASTExpression abstractMultiplicativeExpression(PTNode ptMultiplicativeExpression) {
    assert ptMultiplicativeExpression.is(PTNonterminal.Type.MULTIPLICATIVE_EXPRESSION);

    final ASTBinaryExpression.Builder builder = new ASTBinaryExpression.Builder(ptMultiplicativeExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptMultiplicativeExpression.getChildren());

    builder.withExpression(abstractNotExpression(nodes.next()));

    while (nodes.peek().is(Token.Type.STAR, Token.Type.SLASH, Token.Type.PERCENT)) {
      if (nodes.peek().is(Token.Type.STAR)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.MULTIPLY, abstractNotExpression(nodes.next()));
      } else if (nodes.peek().is(Token.Type.SLASH)) {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.DIVIDE, abstractNotExpression(nodes.next()));
      } else /* if (nodes.peek().is(Token.Type.PERCENT)) */ {
        nodes.next();

        builder.withExpression(ASTBinaryExpression.Type.MODULUS, abstractNotExpression(nodes.next()));
      }
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // NotExpression -> BANG* NegationExpression
  private ASTExpression abstractNotExpression(PTNode ptNotExpression) {
    assert ptNotExpression.is(PTNonterminal.Type.NOT_EXPRESSION);

    final ASTUnaryExpression.Builder builder = new ASTUnaryExpression.Builder();
    final Peekable nodes = Peekable.of(ptNotExpression.getChildren());

    while (nodes.peek().is(Token.Type.BANG)) {
      builder.pushType(nodes.peek().getTextLocation(), ASTUnaryExpression.Type.NOT);
      nodes.next();
    }

    builder.withExpression(abstractNegationExpression(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // NegationExpression -> MINUS* UnitExpression
  private ASTExpression abstractNegationExpression(PTNode ptNegationExpression) {
    assert ptNegationExpression.is(PTNonterminal.Type.NEGATION_EXPRESSION);

    final ASTUnaryExpression.Builder builder = new ASTUnaryExpression.Builder();
    final Peekable nodes = Peekable.of(ptNegationExpression.getChildren());

    while (nodes.peek().is(Token.Type.MINUS)) {
      builder.pushType(nodes.peek().getTextLocation(), ASTUnaryExpression.Type.NEGATE);
      nodes.next();
    }

    builder.withExpression(abstractUnitExpression(nodes.next()));

    assert !nodes.hasNext();

    return builder.build();
  }

  // UnitExpression -> LocationExpression | MethodCallExpression | LengthExpression | Literal | LEFT_ROUND Expression RIGHT_ROUND
  private ASTExpression abstractUnitExpression(PTNode ptUnitExpression) {
    assert ptUnitExpression.is(PTNonterminal.Type.UNIT_EXPRESSION);

    ASTExpression unitExpression;
    final Peekable nodes = Peekable.of(ptUnitExpression.getChildren());

    if (nodes.peek().is(PTNonterminal.Type.LOCATION_EXPRESSION)) {
      unitExpression = abstractLocationExpression(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.METHOD_CALL_EXPRESSION)) {
      unitExpression = abstractMethodCallExpression(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.LENGTH_EXPRESSION)) {
      unitExpression = abstractLengthExpression(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.LITERAL)) {
      unitExpression = abstractLiteral(nodes.next());
    } else if (nodes.peek().is(Token.Type.LEFT_ROUND)) {
      nodes.next();

      unitExpression = abstractExpression(nodes.next());

      assert nodes.peek().is(Token.Type.RIGHT_ROUND);
      nodes.next();
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return unitExpression;
  }

  // LocationExpression -> IDENTIFIER (LEFT_SQUARE Expression RIGHT_SQUARE)?
  private ASTLocationExpression abstractLocationExpression(PTNode ptLocationExpression) {
    assert ptLocationExpression.is(PTNonterminal.Type.LOCATION_EXPRESSION);

    final ASTLocationExpression.Builder builder = new ASTLocationExpression.Builder(ptLocationExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptLocationExpression.getChildren());

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    if (nodes.peek().is(Token.Type.LEFT_SQUARE)) {
      nodes.next();

      builder.withOffset(abstractExpression(nodes.next()));

      assert nodes.peek().is(Token.Type.RIGHT_SQUARE);
      nodes.next();
    }

    assert !nodes.hasNext();

    return builder.build();
  }

  // MethodCallExpression -> IDENTIFIER LEFT_ROUND (Argument (COMMA Argument)*)? RIGHT_ROUND 
  private ASTMethodCallExpression abstractMethodCallExpression(PTNode ptMethodCallExpression) {
    assert ptMethodCallExpression.is(PTNonterminal.Type.METHOD_CALL_EXPRESSION);

    final ASTMethodCallExpression.Builder builder = new ASTMethodCallExpression.Builder(ptMethodCallExpression.getTextLocation());
    final Peekable nodes = Peekable.of(ptMethodCallExpression.getChildren());

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    builder.withIdentifier(nodes.next().getText());

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    if (nodes.peek().is(PTNonterminal.Type.ARGUMENT)) {
      builder.addArgument(abstractArgument(nodes.next()));

      while (nodes.peek().is(Token.Type.COMMA)) {
        nodes.next();

        builder.addArgument(abstractArgument(nodes.next()));
      }
    }

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    assert !nodes.hasNext();

    return builder.build();
  }

  // LengthExpression -> LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND
  private ASTLengthExpression abstractLengthExpression(PTNode ptLengthExpression) {
    assert ptLengthExpression.is(PTNonterminal.Type.LENGTH_EXPRESSION);

    ASTLengthExpression lengthExpression;
    final Peekable nodes = Peekable.of(ptLengthExpression.getChildren());

    assert nodes.peek().is(Token.Type.LEN);
    nodes.next();

    assert nodes.peek().is(Token.Type.LEFT_ROUND);
    nodes.next();

    assert nodes.peek().is(Token.Type.IDENTIFIER);
    lengthExpression = new ASTLengthExpression(ptLengthExpression.getTextLocation(), nodes.next().getText());

    assert nodes.peek().is(Token.Type.RIGHT_ROUND);
    nodes.next();

    assert !nodes.hasNext();

    return lengthExpression;
  }

  // Argument -> Expression | StringLiteral
  private ASTArgument abstractArgument(PTNode ptArgument) {
    assert ptArgument.is(PTNonterminal.Type.ARGUMENT);

    ASTArgument argument;
    final Peekable nodes = Peekable.of(ptArgument.getChildren());

    if (nodes.peek().is(PTNonterminal.Type.EXPRESSION)) {
      argument = abstractExpression(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.STRING_LITERAL)) {
      argument = abstractStringLiteral(nodes.next());
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return argument;
  }

  // Literal -> IntegerLiteral | CharacterLiteral | BooleanLiteral
  private ASTExpression abstractLiteral(PTNode ptLiteral) {
    assert ptLiteral.is(PTNonterminal.Type.LITERAL);

    ASTExpression literal;
    final Peekable nodes = Peekable.of(ptLiteral.getChildren());

    if (nodes.peek().is(PTNonterminal.Type.INTEGER_LITERAL)) {
      literal = abstractIntegerLiteral(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.CHARACTER_LITERAL)) {
      literal = abstractCharacterLiteral(nodes.next());
    } else if (nodes.peek().is(PTNonterminal.Type.BOOLEAN_LITERAL)) {
      literal = abstractBooleanLiteral(nodes.next());
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return literal;
  }

  // IntegerLiteral -> DECIMAL | HEXADECIMAL
  private ASTIntegerLiteral abstractIntegerLiteral(PTNode ptIntegerLiteral) {
    assert ptIntegerLiteral.is(PTNonterminal.Type.INTEGER_LITERAL);

    ASTIntegerLiteral integerLiteral;
    final Peekable nodes = Peekable.of(ptIntegerLiteral.getChildren());

    if (nodes.peek().is(Token.Type.DECIMAL)) {
      integerLiteral = new ASTIntegerLiteral(ptIntegerLiteral.getTextLocation(), parseDecimal(nodes.next().getText()));
    } else if (nodes.peek().is(Token.Type.HEXADECIMAL)) {
      integerLiteral = new ASTIntegerLiteral(ptIntegerLiteral.getTextLocation(), parseHexadecimal(nodes.next().getText()));
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return integerLiteral;
  }

  // CharacterLiteral -> CHARACTER
  private ASTCharacterLiteral abstractCharacterLiteral(PTNode ptCharacterLiteral) {
    assert ptCharacterLiteral.is(PTNonterminal.Type.CHARACTER_LITERAL);

    ASTCharacterLiteral characterLiteral;
    final Peekable nodes = Peekable.of(ptCharacterLiteral.getChildren());

    characterLiteral = new ASTCharacterLiteral(ptCharacterLiteral.getTextLocation(), parseCharacter(nodes.next().getText()));

    assert !nodes.hasNext();

    return characterLiteral;
  }

  // BooleanLiteral -> TRUE | FALSE
  private ASTBooleanLiteral abstractBooleanLiteral(PTNode ptBooleanLiteral) {
    assert ptBooleanLiteral.is(PTNonterminal.Type.BOOLEAN_LITERAL);

    ASTBooleanLiteral booleanLiteral;
    final Peekable nodes = Peekable.of(ptBooleanLiteral.getChildren());

    if (nodes.peek().is(Token.Type.TRUE)) {
      nodes.next();

      booleanLiteral = new ASTBooleanLiteral(ptBooleanLiteral.getTextLocation(), true);
    } else if (nodes.peek().is(Token.Type.FALSE)) {
      nodes.next();

      booleanLiteral = new ASTBooleanLiteral(ptBooleanLiteral.getTextLocation(), false);
    } else {
      throw new RuntimeException("unreachable");
    }

    assert !nodes.hasNext();

    return booleanLiteral;
  }

  // StringLiteral -> STRING
  private ASTStringLiteral abstractStringLiteral(PTNode ptStringLiteral) {
    assert ptStringLiteral.is(PTNonterminal.Type.STRING_LITERAL);

    ASTStringLiteral stringLiteral;
    final Peekable nodes = Peekable.of(ptStringLiteral.getChildren());

    stringLiteral = new ASTStringLiteral(ptStringLiteral.getTextLocation(), parseString(nodes.next().getText()));

    assert !nodes.hasNext();

    return stringLiteral;
  }

  long parseDecimal(String decimal) {
    return Long.decode(decimal);
  }

  long parseHexadecimal(String hexadecimal) {
    return Long.decode(hexadecimal);
  }

  char parseCharacter(String character) {
    return character.charAt(1); // FIXME(rbd): Handle un/escape characters.
  }

  String parseString(String string) {
    return string.substring(1, string.length() - 1); // FIXME(rbd): Handle un/escape characters.
  }

  private static class Peekable {

    private final List<PTNode> nodes;
    private int index;

    public static Peekable of(List<PTNode> nodes) {
      return new Peekable(nodes, 0);
    }

    private Peekable(List<PTNode> nodes, int index) {
      this.nodes = nodes;
      this.index = index;
    }

    public PTNode peek() {
      if (index >= nodes.size()) {
        return PTNode.eos();
      } else {
        return nodes.get(index);
      }
    }

    public PTNode next() {
      if (index >= nodes.size()) {
        return PTNode.eos();
      } else {
        return nodes.get(index++);
      }
    }

    public boolean hasNext() {
      return index < nodes.size();
    }

  }

}
