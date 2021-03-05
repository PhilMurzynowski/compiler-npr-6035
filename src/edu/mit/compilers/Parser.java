package edu.mit.compilers;

import java.util.List;

class Parser {

  private Peekable<Token> tokens;

  public Parser() { }

  // Start -> Program
  public PTNode parse(List<Token> tokens) throws ParserException {
    this.tokens = Peekable.from(tokens);

    PTNode parseTree = parseProgram();

    if (!this.tokens.peek().is(Token.Type.EOF)) {
      throw new ParserException(this.tokens.peek(), ParserException.Type.INCOMPLETE_PARSE, "incomplete parse");
    }

    return parseTree;
  }

  // Program -> ImportDeclaration* FieldMethodDeclaration?
  private PTNode parseProgram() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.PROGRAM);

    while (tokens.peek().is(Token.Type.IMPORT)) {
      builder.addChild(parseImportDeclaration());
    }

    if (tokens.peek().in(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID)) {
      builder.addChild(parseFieldMethodDeclaration());
    }

    return builder.build();
  }

  // ImportDeclaration -> IMPORT IDENTIFIER SEMICOLON
  private PTNode parseImportDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.IMPORT_DECLARATION);

    expect(Token.Type.IMPORT);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // FieldMethodDeclaration -> (INT | BOOL) IDENTIFIER (FieldDeclaration FieldMethodDeclaration? | MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*) | VOID IDENTIFIER MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*
  private PTNode parseFieldMethodDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FIELD_METHOD_DECLARATION);

    if (tokens.peek().in(Token.Type.INT, Token.Type.BOOL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.IDENTIFIER);
      builder.addChild(new PTTerminal(tokens.next()));

      if (tokens.peek().in(Token.Type.LEFT_SQUARE, Token.Type.COMMA, Token.Type.SEMICOLON)) {
        builder.addChild(parseFieldDeclaration());

        if (tokens.peek().in(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID)) {
          builder.addChild(parseFieldMethodDeclaration());
        }
      } else if (tokens.peek().in(Token.Type.LEFT_ROUND)) {
        builder.addChild(parseMethodDeclaration());

        while (tokens.peek().in(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID)) {
          builder.addChild(new PTTerminal(tokens.next()));

          expect(Token.Type.IDENTIFIER);
          builder.addChild(new PTTerminal(tokens.next()));

          builder.addChild(parseMethodDeclaration());
        }
      } else {
        throw exception(Token.Type.LEFT_SQUARE, Token.Type.COMMA, Token.Type.SEMICOLON, Token.Type.LEFT_ROUND);
      }
    } else if (tokens.peek().is(Token.Type.VOID)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.IDENTIFIER);
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseMethodDeclaration());

      while (tokens.peek().in(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID)) {
        builder.addChild(new PTTerminal(tokens.next()));

        expect(Token.Type.IDENTIFIER);
        builder.addChild(new PTTerminal(tokens.next()));

        builder.addChild(parseMethodDeclaration());
      }
    } else {
      throw exception(Token.Type.INT, Token.Type.BOOL, Token.Type.VOID);
    }

    return builder.build();
  }

  // FieldDeclaration -> (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)? (COMMA IDENTIFIER (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)?)* SEMICOLON
  private PTNode parseFieldDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FIELD_DECLARATION);

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.DECIMAL, Token.Type.HEXADECIMAL);
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.RIGHT_SQUARE);
      builder.addChild(new PTTerminal(tokens.next()));
    } 

    while (tokens.peek().is(Token.Type.COMMA)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.IDENTIFIER);
      builder.addChild(new PTTerminal(tokens.next()));

      if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
        builder.addChild(new PTTerminal(tokens.next()));

        expect(Token.Type.DECIMAL, Token.Type.HEXADECIMAL);
        builder.addChild(new PTTerminal(tokens.next()));

        expect(Token.Type.RIGHT_SQUARE);
        builder.addChild(new PTTerminal(tokens.next()));
      } 
    }

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // MethodDeclaration -> LEFT_ROUND ((INT | BOOL) IDENTIFIER (COMMA (INT | BOOL) IDENTIFIER)*)? RIGHT_ROUND Block
  private PTNode parseMethodDeclaration() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_DECLARATION);

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().in(Token.Type.INT, Token.Type.BOOL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.IDENTIFIER);
      builder.addChild(new PTTerminal(tokens.next()));

      while (tokens.peek().in(Token.Type.COMMA)) {
        builder.addChild(new PTTerminal(tokens.next()));

        expect(Token.Type.INT, Token.Type.BOOL);
        builder.addChild(new PTTerminal(tokens.next()));

        expect(Token.Type.IDENTIFIER);
        builder.addChild(new PTTerminal(tokens.next()));
      }
    }

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    return builder.build();
  }

  // Block -> LEFT_CURLY ((INT | BOOL) IDENTIFIER FieldDeclaration)* Statement* RIGHT_CURLY
  private PTNode parseBlock() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BLOCK);

    expect(Token.Type.LEFT_CURLY);
    builder.addChild(new PTTerminal(tokens.next()));

    while (tokens.peek().in(Token.Type.INT, Token.Type.BOOL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      expect(Token.Type.IDENTIFIER);
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseFieldDeclaration());
    }

    while (tokens.peek().in(Token.Type.IDENTIFIER, Token.Type.IF, Token.Type.FOR, Token.Type.WHILE, Token.Type.RETURN, Token.Type.BREAK, Token.Type.CONTINUE)) {
      builder.addChild(parseStatement());
    }

    expect(Token.Type.RIGHT_CURLY);
    builder.addChild(new PTTerminal(tokens.next()));
    
    return builder.build();
  }

  // Statement -> AssignMethodCallStatement | IfStatement | ForStatement | WhileStatement | ReturnStatement | BreakStatement | ContinueStatement
  private PTNode parseStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.STATEMENT);

    if (tokens.peek().is(Token.Type.IDENTIFIER)) {
      builder.addChild(parseAssignMethodCallStatement());
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

  // AssignMethodCallStatement -> IDENTIFIER (AssignStatement | MethodCallStatement)
  private PTNode parseAssignMethodCallStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ASSIGN_METHOD_CALL_STATEMENT);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().in(Token.Type.LEFT_SQUARE, Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
      builder.addChild(parseAssignStatement());
    } else if (tokens.peek().is(Token.Type.LEFT_ROUND)) {
      builder.addChild(parseMethodCallStatement());
    } else {
      throw exception(Token.Type.LEFT_SQUARE, Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS, Token.Type.LEFT_ROUND);
    }

    return builder.build();
  }

  // AssignStatement -> LocationExpression? ((EQUAL | PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS)) SEMICOLON
  private PTNode parseAssignStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ASSIGN_STATEMENT);

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      builder.addChild(parseLocationExpression());
    }

    if (tokens.peek().in(Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseExpression());
    } else if (tokens.peek().in(Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));
    } else {
      throw exception(Token.Type.EQUAL, Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS);
    }

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // MethodCallStatement -> MethodCallExpression SEMICOLON
  private PTNode parseMethodCallStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_CALL_STATEMENT);

    builder.addChild(parseMethodCallExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // IfStatement -> IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
  private PTNode parseIfStatement() throws ParserException {
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

  // ForStatement -> FOR LEFT_ROUND IDENTIFIER EQUAL Expression SEMICOLON Expression SEMICOLON IDENTIFIER LocationExpression? ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS)) RIGHT_ROUND Block
  private PTNode parseForStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.FOR_STATEMENT);

    expect(Token.Type.FOR);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.EQUAL);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      builder.addChild(parseLocationExpression());
    }

    if (tokens.peek().in(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseExpression());
    } else if (tokens.peek().in(Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));
    } else {
      throw exception(Token.Type.PLUS_EQUAL, Token.Type.MINUS_EQUAL, Token.Type.PLUS_PLUS, Token.Type.MINUS_MINUS);
    }

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseBlock());

    return builder.build();
  }

  // WhileStatement -> WHILE LEFT_ROUND Expression RIGHT_ROUND Block
  private PTNode parseWhileStatement() throws ParserException {
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
  private PTNode parseReturnStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.RETURN_STATEMENT);

    expect(Token.Type.RETURN);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().in(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND)) {
      builder.addChild(parseExpression());
    }

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // BreakStatement -> BREAK SEMICOLON
  private PTNode parseBreakStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BREAK_STATEMENT);

    expect(Token.Type.BREAK);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // ContinueStatement -> CONTINUE SEMICOLON
  private PTNode parseContinueStatement() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.CONTINUE_STATEMENT);

    expect(Token.Type.CONTINUE);
    builder.addChild(new PTTerminal(tokens.next()));

    expect(Token.Type.SEMICOLON);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // Expression -> OrExpression
  private PTNode parseExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.EXPRESSION);

    builder.addChild(parseOrExpression());

    return builder.build();
  }

  // OrExpression -> AndExpression (VERTICAL_VERTICAL AndExpression)*
  private PTNode parseOrExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.OR_EXPRESSION);

    builder.addChild(parseAndExpression());

    while (tokens.peek().is(Token.Type.VERTICAL_VERTICAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseAndExpression());
    }

    return builder.build();
  }

  // AndExpression -> EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
  private PTNode parseAndExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.AND_EXPRESSION);

    builder.addChild(parseEqualityExpression());

    while (tokens.peek().is(Token.Type.AMPERSAND_AMPERSAND)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseEqualityExpression());
    }

    return builder.build();
  }

  // EqualityExpression -> RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
  private PTNode parseEqualityExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.EQUALITY_EXPRESSION);

    builder.addChild(parseRelationalExpression());

    while (tokens.peek().in(Token.Type.EQUAL_EQUAL, Token.Type.BANG_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseRelationalExpression());
    }

    return builder.build();
  }

  // RelationalExpression -> AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
  private PTNode parseRelationalExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.RELATIONAL_EXPRESSION);

    builder.addChild(parseAdditiveExpression());

    while (tokens.peek().in(Token.Type.LESS, Token.Type.LESS_EQUAL, Token.Type.GREATER, Token.Type.GREATER_EQUAL)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseAdditiveExpression());
    }

    return builder.build();
  }

  // AdditiveExpression -> MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
  private PTNode parseAdditiveExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.ADDITIVE_EXPRESSION);

    builder.addChild(parseMultiplicativeExpression());

    while (tokens.peek().in(Token.Type.PLUS, Token.Type.MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseMultiplicativeExpression());
    }

    return builder.build();
  }

  // MultiplicativeExpression -> NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
  private PTNode parseMultiplicativeExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.MULTIPLICATIVE_EXPRESSION);

    builder.addChild(parseNotExpression());

    while (tokens.peek().in(Token.Type.STAR, Token.Type.SLASH, Token.Type.PERCENT)) {
      builder.addChild(new PTTerminal(tokens.next()));

      builder.addChild(parseNotExpression());
    }

    return builder.build();
  }

  // NotExpression -> BANG* NegationExpression
  private PTNode parseNotExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.NOT_EXPRESSION);

    while (tokens.peek().is(Token.Type.BANG)) {
      builder.addChild(new PTTerminal(tokens.next()));
    }

    builder.addChild(parseNegationExpression());

    return builder.build();
  }

  // NegationExpression -> MINUS* UnitExpression
  private PTNode parseNegationExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.NEGATION_EXPRESSION);

    while (tokens.peek().is(Token.Type.MINUS)) {
      builder.addChild(new PTTerminal(tokens.next()));
    }

    builder.addChild(parseUnitExpression());

    return builder.build();
  }

  // UnitExpression -> LocationMethodCallExpression | LengthExpression | Literal | LEFT_ROUND Expression RIGHT_ROUND
  private PTNode parseUnitExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.UNIT_EXPRESSION);

    if (tokens.peek().is(Token.Type.IDENTIFIER)) {
      builder.addChild(parseLocationMethodCallExpression());
    } else if (tokens.peek().is(Token.Type.LEN)) {
      builder.addChild(parseLengthExpression());
    } else if (tokens.peek().in(Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE)) {
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

  // LocationMethodCallExpression -> IDENTIFIER (LocationExpression? | MethodCallExpression)
  private PTNode parseLocationMethodCallExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LOCATION_METHOD_CALL_EXPRESSION);

    expect(Token.Type.IDENTIFIER);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().is(Token.Type.LEFT_SQUARE)) {
      builder.addChild(parseLocationExpression());
    } else if (tokens.peek().is(Token.Type.LEFT_ROUND)) {
      builder.addChild(parseMethodCallExpression());
    }

    return builder.build();
  }

  // LocationExpression -> LEFT_SQUARE Expression RIGHT_SQUARE
  private PTNode parseLocationExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LOCATION_EXPRESSION);

    expect(Token.Type.LEFT_SQUARE);
    builder.addChild(new PTTerminal(tokens.next()));

    builder.addChild(parseExpression());

    expect(Token.Type.RIGHT_SQUARE);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // MethodCallExpression -> LEFT_ROUND ((Expression | STRING) (COMMA (Expression | STRING))*)? RIGHT_ROUND 
  private PTNode parseMethodCallExpression() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.METHOD_CALL_EXPRESSION);

    expect(Token.Type.LEFT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    if (tokens.peek().in(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND, Token.Type.STRING)) {
      if (tokens.peek().in(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND)) {
        builder.addChild(parseExpression());
      } else if (tokens.peek().is(Token.Type.STRING)) {
        builder.addChild(new PTTerminal(tokens.next()));
      }

      while (tokens.peek().is(Token.Type.COMMA)) {
        builder.addChild(new PTTerminal(tokens.next()));

        if (tokens.peek().in(Token.Type.BANG, Token.Type.MINUS, Token.Type.IDENTIFIER, Token.Type.LEN, Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE, Token.Type.LEFT_ROUND)) {
          builder.addChild(parseExpression());
        } else if (tokens.peek().is(Token.Type.STRING)) {
          builder.addChild(new PTTerminal(tokens.next()));
        }
      }
    }

    expect(Token.Type.RIGHT_ROUND);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // LengthExpression -> LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND
  private PTNode parseLengthExpression() throws ParserException {
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

  // Literal -> IntegerLiteral | CharacterLiteral | BooleanLiteral
  private PTNode parseLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.LITERAL);

    if (tokens.peek().in(Token.Type.DECIMAL, Token.Type.HEXADECIMAL)) {
      builder.addChild(parseIntegerLiteral());
    } else if (tokens.peek().is(Token.Type.CHARACTER)) {
      builder.addChild(parseCharacterLiteral());
    } else if (tokens.peek().in(Token.Type.TRUE, Token.Type.FALSE)) {
      builder.addChild(parseBooleanLiteral());
    } else {
      throw exception(Token.Type.DECIMAL, Token.Type.HEXADECIMAL, Token.Type.CHARACTER, Token.Type.TRUE, Token.Type.FALSE);
    }

    return builder.build();
  }

  // IntegerLiteral -> DECIMAL | HEXADECIMAL
  private PTNode parseIntegerLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.INTEGER_LITERAL);

    expect(Token.Type.DECIMAL, Token.Type.HEXADECIMAL);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // CharacterLiteral -> CHARACTER
  private PTNode parseCharacterLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.CHARACTER_LITERAL);

    expect(Token.Type.CHARACTER);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
  }

  // BooleanLiteral -> TRUE | FALSE
  private PTNode parseBooleanLiteral() throws ParserException {
    PTNonterminal.Builder builder = new PTNonterminal.Builder(PTNonterminal.Type.BOOLEAN_LITERAL);

    expect(Token.Type.TRUE, Token.Type.FALSE);
    builder.addChild(new PTTerminal(tokens.next()));

    return builder.build();
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
    if (!tokens.peek().in(tokenTypes)) {
      throw exception(tokenTypes);
    }
  }

  private ParserException exception(Token.Type ...tokenTypes) {
    if (tokens.peek().is(Token.Type.EOF)) {
      return new ParserException(tokens.peek(), ParserException.Type.UNEXPECTED_EOF, message(tokenTypes));
    } else {
      return new ParserException(tokens.peek(), ParserException.Type.INVALID_TOKEN, message(tokenTypes));
    }
  }

}
