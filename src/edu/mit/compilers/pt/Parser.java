package edu.mit.compilers.pt;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import edu.mit.compilers.tk.*;

public class Parser {

  private Peekable tokens;
  private final List<ParserException> exceptions;

  public Parser() {
    exceptions = new ArrayList<>();
  }

  // Start -> Program
  public Result parseAll(List<Token> tokens) {
    clear();

    this.tokens = Peekable.of(tokens);

    PTNonterminal parseTree = parseProgram();

    if (!this.tokens.peek().is(Token.Type.EOF)) {
      exceptions.add(new ParserException(this.tokens.peek(), ParserException.Type.INCOMPLETE_PARSE, "incomplete parse"));
    }

    return new Result(parseTree, exceptions);
  }

  // Program -> ImportDeclaration* FieldDeclaration* MethodDeclaration*
  private PTNonterminal parseProgram() {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.PROGRAM);

    while (true) {
      while (tokens.peek().is(Token.Type.IMPORT)) {
        try {
          builder.addChild(parseImportDeclaration());
        } catch (ParserException exception) {
          exceptions.add(exception);
          recover(Token.Type.SEMICOLON);
        }
      }
    
      if (tokens.peek().is(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID, Token.Type.EOF)) {
        break;
      }

      exceptions.add(exception(Token.Type.IMPORT, Token.Type.INT, Token.Type.BOOL, Token.Type.VOID));
      recover(Token.Type.SEMICOLON);
    }

    while (true) {
      while (tokens.peek(2).is(Token.Type.LEFT_SQUARE, Token.Type.COMMA, Token.Type.SEMICOLON)) {
        try {
          builder.addChild(parseFieldDeclaration());
        } catch (ParserException exception) {
          exceptions.add(exception);
          recover(Token.Type.SEMICOLON);
        }
      }

      if (tokens.peek(2).is(Token.Type.LEFT_ROUND, Token.Type.EOF)) {
        break;
      }
    
      exceptions.add(exception(2, Token.Type.LEFT_SQUARE, Token.Type.COMMA, Token.Type.SEMICOLON, Token.Type.LEFT_ROUND));
      recover(Token.Type.SEMICOLON);
    }

    while (true) {
      while (tokens.peek(2).is(Token.Type.LEFT_ROUND)) {
        try {
          builder.addChild(parseMethodDeclaration());
        } catch (ParserException exception) {
          exceptions.add(exception);
          recover(Token.Type.RIGHT_CURLY);
        }
      }

      if (tokens.peek(2).is(Token.Type.EOF)) {
        break;
      }
    
      exceptions.add(exception(2, Token.Type.LEFT_ROUND));
      recover(Token.Type.RIGHT_CURLY);
    }

    return builder.build();
  }

  // ImportDeclaration -> IMPORT IDENTIFIER SEMICOLON
  private PTNonterminal parseImportDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.IMPORT_DECLARATION);

    expect(Token.Type.IMPORT);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // FieldDeclaration -> (INT | BOOL) FieldIdentifierDeclaration (COMMA FieldIdentifierDeclaration)* SEMICOLON
  private PTNonterminal parseFieldDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FIELD_DECLARATION);

    expect(Token.Type.INT, Token.Type.BOOL);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseFieldIdentifierDeclaration());

    while (tokens.peek().is(Token.Type.COMMA)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseFieldIdentifierDeclaration());
    }

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // MethodDeclaration -> (INT | BOOL | VOID) IDENTIFIER LEFT_ROUND (ArgumentDeclaration (COMMA ArgumentDeclaration)*)? RIGHT_ROUND Block
  private PTNonterminal parseMethodDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_DECLARATION);

    expect(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.INT, Token.Type.BOOL)) {
      builder.addChild(parseArgumentDeclaration());

      while (tokens.peek().is(Token.Type.COMMA)) {
        builder.addChild(new PTTerminal(tokens.next()));

        builder.addChild(parseArgumentDeclaration());
      }
    }

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    return builder.build();
  }

  // FieldIdentifierDeclaration -> IDENTIFIER (LEFT_SQUARE IntegerLiteral RIGHT_SQUARE)?
  private PTNonterminal parseFieldIdentifierDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FIELD_IDENTIFIER_DECLARATION);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseIntegerLiteral());

      expect(Token.Type.RIGHT_SQUARE);
      builder.addChild(new PTTerminal(tokens.next()));
    } 

    return builder.build();
  }

  // ArgumentDeclaration -> (INT | BOOL) IDENTIFIER
  private PTNonterminal parseArgumentDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ARGUMENT_DECLARATION);
    
    expect(Token.Type.INT, Token.Type.BOOL);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // Block -> LEFT_CURLY FieldDeclaration* Statement* RIGHT_CURLY
  private PTNonterminal parseBlock() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BLOCK);

    expect(Token.Type.LEFT_CURLY);
    builder.addChild(new PTTerminal(tokens.next()));

    while (tokens.peek().is(Token.Type.INT, Token.Type.BOOL)) {
      try {
        builder.addChild(parseFieldDeclaration());
      } catch (ParserException exception) {
        exceptions.add(exception);
        recover(Token.Type.SEMICOLON);
      }
    }

    while (tokens.peek().is(Token.Type.IDENTIFIER, Token.Type.IF, Token.Type.FOR, Token.Type.WHILE, Token.Type.RETURN, Token.Type.BREAK, Token.Type.CONTINUE)) {
      if (tokens.peek().is(Token.Type.IDENTIFIER, Token.Type.RETURN, Token.Type.BREAK, Token.Type.CONTINUE)) {
        try {
          builder.addChild(parseStatement());
        } catch (ParserException exception) {
          exceptions.add(exception);
          recover(Token.Type.SEMICOLON);
        }
      } else /* if (tokens.peek().is(Token.Type.IF, Token.Type.FOR, Token.Type.WHILE)) */ {
        try {
          builder.addChild(parseStatement());
        } catch (ParserException exception) {
          exceptions.add(exception);
          recover(Token.Type.RIGHT_CURLY);
          if (tokens.peek().is(Token.Type.ELSE)) {
            recover(Token.Type.RIGHT_CURLY);
          }
        }
      }
    }

    expect(Token.Type.RIGHT_CURLY);
    builder.addChild(new PTTerminal(tokens.next()));
    
    return builder.build();
  }

  // Statement -> IDAssignStatement | AssignStatement | CompoundAssignStatement | MethodCallStatement | IfStatement | ForStatement | WhileStatement | ReturnStatement | BreakStatement | ContinueStatement
  private PTNonterminal parseStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.STATEMENT);

    if (tokens.peek().is(Token.Type.IDENTIFIER)) {
      if (tokens.peek(1).is(Token.Type.LEFT_SQUARE)) {
        // NOTE(rbd): This is an unfortunate hack because of unknown lookahead to choose between AssignStatement and
        // CompoundAssignStatement where the location expression involves an offset. I prefer keeping this distinction
        // between assign statements to make the ForStatement easier, but maybe there is a better way.

        PTNonterminal locationExpression = parseLocationExpression();

        if (tokens.peek().is(Token.Type.EQUAL)) {
          builder.addChild(parseAssignStatement(Optional.of(locationExpression)));
        } else if (tokens.peek().is(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
          builder.addChild(parseCompoundAssignStatement(Optional.of(locationExpression)));
        } else {
          throw exception(Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS);
        }
      } else if (tokens.peek(1).is(Token.Type.EQUAL)) {
        builder.addChild(parseIDAssignStatement());
      } else if (tokens.peek(1).is(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
        builder.addChild(parseCompoundAssignStatement(Optional.empty()));
      } else if (tokens.peek(1).is(Token.Type.LEFT_ROUND)) {
        builder.addChild(parseMethodCallStatement());
      } else {
        throw exception(1, Token.Type.LEFT_SQUARE, Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS, Token.Type.LEFT_ROUND);
      }
    } else if (tokens.peek().is(Token.Type.IF)) {
      builder.addChild(parseIfStatement());
    } else if (tokens.peek().is(Token.Type.FOR)) {
      builder.addChild(parseForStatement());
    } else if (tokens.peek().is(Token.Type.WHILE)) {
      builder.addChild(parseWhileStatement());
    } else if (tokens.peek().is(Token.Type.RETURN)) {
      builder.addChild(parseReturnStatement());
    } else if (tokens.peek().is(Token.Type.BREAK)) {
      builder.addChild(parseBreakStatement());
    } else if (tokens.peek().is(Token.Type.CONTINUE)) {
      builder.addChild(parseContinueStatement());
    } else {
      throw exception(Token.Type.IDENTIFIER, Token.Type.IF, Token.Type.FOR, Token.Type.WHILE, Token.Type.RETURN, Token.Type.BREAK, Token.Type.CONTINUE);
    }

    return builder.build();
  }

  // IDAssignStatement -> IDAssignExpression SEMICOLON
  private PTNonterminal parseIDAssignStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ID_ASSIGN_STATEMENT);

    builder.addChild(parseIDAssignExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // AssignStatement -> LocationExpression EQUAL Expression SEMICOLON
  private PTNonterminal parseAssignStatement(Optional<PTNonterminal> locationExpression) throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ASSIGN_STATEMENT);

    if (locationExpression.isPresent()) {
      builder.addChild(locationExpression.get());
    } else {
      builder.addChild(parseLocationExpression());
    }

    expect(Token.Type.EQUAL);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // CompoundAssignStatement -> CompoundAssignExpression SEMICOLON
  private PTNonterminal parseCompoundAssignStatement(Optional<PTNonterminal> locationExpression) throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.COMPOUND_ASSIGN_STATEMENT);

    builder.addChild(parseCompoundAssignExpression(locationExpression));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // MethodCallStatement -> MethodCallExpression SEMICOLON
  private PTNonterminal parseMethodCallStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_CALL_STATEMENT);

    builder.addChild(parseMethodCallExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // IfStatement -> IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
  private PTNonterminal parseIfStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.IF_STATEMENT);

    expect(Token.Type.IF);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    if (tokens.peek().is(Token.Type.ELSE)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseBlock());
    }

    return builder.build();
  }

  // ForStatement -> FOR LEFT_ROUND IDAssignExpression SEMICOLON Expression SEMICOLON CompoundAssignExpression RIGHT_ROUND Block
  private PTNonterminal parseForStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FOR_STATEMENT);

    expect(Token.Type.FOR);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseIDAssignExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseCompoundAssignExpression(Optional.empty()));

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    return builder.build();
  }

  // WhileStatement -> WHILE LEFT_ROUND Expression RIGHT_ROUND Block
  private PTNonterminal parseWhileStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.WHILE_STATEMENT);

    expect(Token.Type.WHILE);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    return builder.build();
  }

  // ReturnStatement -> RETURN (Expression)? SEMICOLON
  private PTNonterminal parseReturnStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.RETURN_STATEMENT);

    expect(Token.Type.RETURN);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND)) {
      builder.addChild(parseExpression());
    }

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // BreakStatement -> BREAK SEMICOLON
  private PTNonterminal parseBreakStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BREAK_STATEMENT);

    expect(Token.Type.BREAK);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // ContinueStatement -> CONTINUE SEMICOLON
  private PTNonterminal parseContinueStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.CONTINUE_STATEMENT);

    expect(Token.Type.CONTINUE);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // IDAssignExpression -> IDENTIFIER EQUAL Expression
  private PTNonterminal parseIDAssignExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ID_ASSIGN_EXPRESSION);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.EQUAL);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    return builder.build();
  }

  // CompoundAssignExpression -> LocationExpression ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS))
  private PTNonterminal parseCompoundAssignExpression(Optional<PTNonterminal> locationExpression) throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.COMPOUND_ASSIGN_EXPRESSION);

    if (locationExpression.isPresent()) {
      builder.addChild(locationExpression.get());
    } else {
      builder.addChild(parseLocationExpression());
    }

    if (tokens.peek().is(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseExpression());
    } else if (tokens.peek().is(Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));
    } else {
      throw exception(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS);
    }

    return builder.build();
  }

  // Expression -> OrExpression
  private PTNonterminal parseExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.EXPRESSION);

    builder.addChild(parseOrExpression());

    return builder.build();
  }

  // OrExpression -> AndExpression (VERTICAL_VERTICAL AndExpression)*
  private PTNonterminal parseOrExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.OR_EXPRESSION);

    builder.addChild(parseAndExpression());

    while (tokens.peek().is(Token.Type.VERTICAL_VERTICAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseAndExpression());
    }

    return builder.build();
  }

  // AndExpression -> EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
  private PTNonterminal parseAndExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.AND_EXPRESSION);

    builder.addChild(parseEqualityExpression());

    while (tokens.peek().is(Token.Type.AMPERSAND_AMPERSAND)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseEqualityExpression());
    }

    return builder.build();
  }

  // EqualityExpression -> RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
  private PTNonterminal parseEqualityExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.EQUALITY_EXPRESSION);

    builder.addChild(parseRelationalExpression());

    while (tokens.peek().is(Token.Type.EQUAL_EQUAL, Token.Type.BANG_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseRelationalExpression());
    }

    return builder.build();
  }

  // RelationalExpression -> AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
  private PTNonterminal parseRelationalExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.RELATIONAL_EXPRESSION);

    builder.addChild(parseAdditiveExpression());

    while (tokens.peek().is(Token.Type.LESS, Token.Type.LESS_EQUAL, Token.Type.GREATER, Token.Type.GREATER_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseAdditiveExpression());
    }

    return builder.build();
  }

  // AdditiveExpression -> MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
  private PTNonterminal parseAdditiveExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ADDITIVE_EXPRESSION);

    builder.addChild(parseMultiplicativeExpression());

    while (tokens.peek().is(Token.Type.PLUS, Token.Type.MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseMultiplicativeExpression());
    }

    return builder.build();
  }

  // MultiplicativeExpression -> NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
  private PTNonterminal parseMultiplicativeExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.MULTIPLICATIVE_EXPRESSION);

    builder.addChild(parseNotExpression());

    while (tokens.peek().is(Token.Type.STAR, Token.Type.SLASH, Token.Type.PERCENT)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseNotExpression());
    }

    return builder.build();
  }

  // NotExpression -> BANG* NegationExpression
  private PTNonterminal parseNotExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.NOT_EXPRESSION);

    while (tokens.peek().is(Token.Type.BANG)) {
      builder.addChild(new PTTerminal(tokens.next()));
    }

    builder.addChild(parseNegationExpression());

    return builder.build();
  }

  // NegationExpression -> MINUS* UnitExpression
  private PTNonterminal parseNegationExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.NEGATION_EXPRESSION);

    while (tokens.peek().is(Token.Type.MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));
    }

    builder.addChild(parseUnitExpression());

    return builder.build();
  }

  // UnitExpression -> LocationExpression | MethodCallExpression | LengthExpression | Literal | LEFT_ROUND Expression RIGHT_ROUND
  private PTNonterminal parseUnitExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.UNIT_EXPRESSION);

    if (tokens.peek().is(Token.Type.IDENTIFIER)) {
      if (tokens.peek(1).is(Token.Type.LEFT_ROUND)) {
        builder.addChild(parseMethodCallExpression());
      } else {
        builder.addChild(parseLocationExpression());
      }
    } else if (tokens.peek().is(Token.Type.LEN)) {
      builder.addChild(parseLengthExpression());
    } else if (tokens.peek().is(Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE)) {
      builder.addChild(parseLiteral());
    } else if (tokens.peek().is(Token.Type.LEFT_ROUND)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseExpression());

      expect(Token.Type.RIGHT_ROUND);
      builder.addChild(new PTTerminal(tokens.next()));
    } else {
      throw exception(Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND);
    }

    return builder.build();
  }

  // LocationExpression -> IDENTIFIER (LEFT_SQUARE Expression RIGHT_SQUARE)?
  private PTNonterminal parseLocationExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LOCATION_EXPRESSION);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      expect(Token.Type.LEFT_SQUARE);
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseExpression());

      expect(Token.Type.RIGHT_SQUARE);
      builder.addChild(new PTTerminal(tokens.next()));
    }

    return builder.build();
  }

  // MethodCallExpression -> IDENTIFIER LEFT_ROUND (Argument (COMMA Argument)*)? RIGHT_ROUND 
  private PTNonterminal parseMethodCallExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_CALL_EXPRESSION);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND, Token.Type.STRING)) {
      builder.addChild(parseArgument());

      while (tokens.peek().is(Token.Type.COMMA)) {
        builder.addChild(new PTTerminal(tokens.next()));

        builder.addChild(parseArgument());
      }
    }

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // LengthExpression -> LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND
  private PTNonterminal parseLengthExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LENGTH_EXPRESSION);

    expect(Token.Type.LEN);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // Argument -> Expression | StringLiteral
  private PTNonterminal parseArgument() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ARGUMENT);

    if (tokens.peek().is(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND)) {
      builder.addChild(parseExpression());
    } else if (tokens.peek().is(Token.Type.STRING)) {
      builder.addChild(parseStringLiteral());
    } else {
      throw exception(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND, Token.Type.STRING);
    }

    return builder.build();
  }

  // Literal -> IntegerLiteral | CharacterLiteral | BooleanLiteral
  private PTNonterminal parseLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LITERAL);

    if (tokens.peek().is(Token.Type.DECIMAL, Token.Type.HEXADECIMAL)) {
      builder.addChild(parseIntegerLiteral());
    } else if (tokens.peek().is(Token.Type.CHARACTER)) {
      builder.addChild(parseCharacterLiteral());
    } else if (tokens.peek().is(Token.Type.TRUE, Token.Type.FALSE)) {
      builder.addChild(parseBooleanLiteral());
    } else {
      throw exception(Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE);
    }

    return builder.build();
  }

  // IntegerLiteral -> DECIMAL | HEXADECIMAL
  private PTNonterminal parseIntegerLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.INTEGER_LITERAL);

    expect(Token.Type.DECIMAL, Token.Type.HEXADECIMAL);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // CharacterLiteral -> CHARACTER
  private PTNonterminal parseCharacterLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.CHARACTER_LITERAL);

    expect(Token.Type.CHARACTER);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // BooleanLiteral -> TRUE | FALSE
  private PTNonterminal parseBooleanLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BOOLEAN_LITERAL);

    expect(Token.Type.TRUE, Token.Type.FALSE);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // StringLiteral -> STRING
  private PTNonterminal parseStringLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.STRING_LITERAL);

    expect(Token.Type.STRING);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  private void clear() {
    exceptions.clear();
  }

  private String message(Token.Type ...tokenTypes) {
    StringBuilder message = new StringBuilder();
    message.append("expected { ");
    for (Token.Type tokenType : tokenTypes) {
      message.append(tokenType + ", ");
    }
    message.append("}");
    return message.toString();
  }

  private void expect(Token.Type ...tokenTypes) throws ParserException {
    if (!tokens.peek().is(tokenTypes)) {
      throw exception(tokenTypes);
    }
  }

  private ParserException exception(int lookahead, Token.Type ...tokenTypes) {
    if (tokens.peek(lookahead).is(Token.Type.EOF)) {
      return new ParserException(tokens.peek(lookahead), ParserException.Type.UNEXPECTED_EOF, message(tokenTypes));
    } else {
      return new ParserException(tokens.peek(lookahead), ParserException.Type.INVALID_TOKEN, message(tokenTypes));
    }
  }

  private ParserException exception(Token.Type ...tokenTypes) {
    return exception(0, tokenTypes);
  }

  private void recover(Token.Type tokenType) {
    if (tokenType.equals(Token.Type.RIGHT_CURLY)) {
      // NOTE(rbd): Attempt to match curly brackets
      while (!tokens.next().is(Token.Type.LEFT_CURLY, Token.Type.EOF)) { }
      for (int curlies = 1; curlies > 0 && !tokens.peek().is(Token.Type.EOF); ) {
        if (tokens.peek().is(Token.Type.LEFT_CURLY)) {
          tokens.next();
          ++curlies;
        } else if (tokens.peek().is(Token.Type.RIGHT_CURLY)) {
          tokens.next();
          --curlies;
        } else {
          tokens.next();
        }
      }
    } else {
      while (!tokens.next().is(tokenType, Token.Type.EOF)) { }
    }
  }

  private static class Peekable {

    private final List<Token> tokens;
    private int index;

    public static Peekable of(List<Token> tokens) {
      return new Peekable(tokens, 0);
    }

    private Peekable(List<Token> tokens, int index) {
      this.tokens = tokens;
      this.index = index;
    }

    public Token peek(int offset) {
      if (index + offset >= tokens.size()) {
        return tokens.get(tokens.size() - 1);
      } else {
        return tokens.get(index + offset);
      }
    }

    public Token peek() {
      return peek(0);
    }

    public Token next() {
      if (index >= tokens.size()) {
        return tokens.get(tokens.size() - 1);
      } else {
        return tokens.get(index++);
      }
    }

  }

  public static class Result {

    private final PTNode parseTree;
    private final List<ParserException> exceptions;

    private Result(PTNode parseTree, List<ParserException> exceptions) {
      this.parseTree = parseTree;
      this.exceptions = List.copyOf(exceptions);
    }

    public PTNode getParseTree() {
      return parseTree;
    }

    public boolean hasExceptions() {
      return !exceptions.isEmpty();
    }

    public List<ParserException> getExceptions() {
      return exceptions;
    }

  }

}
