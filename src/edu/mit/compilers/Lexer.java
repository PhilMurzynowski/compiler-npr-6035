package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

class Lexer {

  private List<Token> tokens;
  private final StringBuilder text;

  public Lexer() {
    this.text = new StringBuilder();
  }

  private interface LexFunction {
    public LexFunction apply(Optional<Character> character) throws LexerException;
  }

  public List<Token> lex(String text) throws LexerException {
    this.tokens = new ArrayList<Token>();

    LexFunction lexFunction = this::lexEmpty;
    for (char c : text.toCharArray()) {
      lexFunction = lexFunction.apply(Optional.of(c));
    }
    lexFunction.apply(Optional.empty());

    return this.tokens;
  }

  private LexFunction done(Token.Type tokenType, Optional<Character> character) {
    this.text.append(character.get());
    this.tokens.add(new Token(tokenType, this.text.toString()));
    return this::lexEmpty;
  }

  private LexFunction next(LexFunction lexFunction, Optional<Character> character) {
    this.text.append(character.get());
    return lexFunction;
  }

  private LexFunction eof(Token.Type tokenType) {
    this.tokens.add(new Token(tokenType, this.text.toString()));
    return this::lexEOF;
  }

  private LexFunction forward(Token.Type tokenType, Optional<Character> character) throws LexerException {
    this.tokens.add(new Token(tokenType, this.text.toString()));
    return this.lexEmpty(character);
  }

  private LexFunction lexEmpty(Optional<Character> character) throws LexerException {
    this.text.setLength(0);

    if (!character.isPresent()) {
      return this::lexEOF;
    }

    switch (character.get()) {
      case ';':
        return this.done(Token.Type.SEMICOLON, character);
      case '[':
        return this.done(Token.Type.LEFT_SQUARE, character);
      case ']':
        return this.done(Token.Type.RIGHT_SQUARE, character);
      case ',':
        return this.done(Token.Type.COMMA, character);
      case '(':
        return this.done(Token.Type.LEFT_ROUND, character);
      case ')':
        return this.done(Token.Type.RIGHT_ROUND, character);
      case '{':
        return this.done(Token.Type.LEFT_CURLY, character);
      case '}':
        return this.done(Token.Type.RIGHT_CURLY, character);
      case '%':
        return this.done(Token.Type.PERCENT, character);
      case '*':
        return this.done(Token.Type.STAR, character);
      case '&':
        return this.next(this.lexGroup1('&', Token.Type.AMPERSAND_AMPERSAND), character);
      case '|':
        return this.next(this.lexGroup1('|', Token.Type.VERTICAL_VERTICAL), character);
      case '=':
        return this.next(this.lexGroup2(Token.Type.EQUAL, Token.Type.EQUAL_EQUAL), character);
      case '!':
        return this.next(this.lexGroup2(Token.Type.BANG, Token.Type.BANG_EQUAL), character);
      case '<':
        return this.next(this.lexGroup2(Token.Type.LESS_THAN, Token.Type.LESS_THAN_EQUAL), character);
      case '>':
        return this.next(this.lexGroup2(Token.Type.GREATER_THAN_EQUAL, Token.Type.GREATER_THAN_EQUAL), character);
      case '+':
        return this.next(this.lexGroup3('+', Token.Type.PLUS, Token.Type.PLUS_EQUAL, Token.Type.PLUS_PLUS), character);
      case '-':
        return this.next(this.lexGroup3('-', Token.Type.MINUS, Token.Type.MINUS_EQUAL, Token.Type.MINUS_MINUS), character);
      case '0':
        return this.next(this::lexZero, character);
      case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
        return this.next(this::lexDecimal, character);
      case '\'':
        return this.next(this::lexCharacter, character);
      case '\"':
        return this.next(this::lexString, character);
      case '/':
        return this.next(this::lexSlash, character);
      case 'b':
        return this.next(this::lexB, character);
      case 'c':
        return this.next(this.lexKeyword("continue", Token.Type.CONTINUE), character);
      case 'e':
        return this.next(this.lexKeyword("else", Token.Type.ELSE), character);
      case 'f':
        return this.next(this::lexF, character);
      case 'i':
        return this.next(this::lexI, character);
      case 'l':
        return this.next(this.lexKeyword("len", Token.Type.LEN), character);
      case 'r':
        return this.next(this.lexKeyword("return", Token.Type.RETURN), character);
      case 't':
        return this.next(this.lexKeyword("true", Token.Type.TRUE), character);
      case 'v':
        return this.next(this.lexKeyword("void", Token.Type.VOID), character);
      case 'w':
        return this.next(this.lexKeyword("while", Token.Type.WHILE), character);
      case 'a': case 'd': case 'g': case 'h': case 'j': case 'k': case 'm': case 'n': case 'o': case 'p': case 'q': 
      case 's': case 'u': case 'x': case 'y': case 'z': case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': 
      case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': 
      case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': case '_':
        return this.next(this::lexIdentifier, character);
      case ' ': case '\t': case '\n':
        return this.next(this::lexWhitespace, character);
    }

    throw new LexerException(LexerException.Type.INVALID_CHARACTER);
  }

  private LexFunction lexEOF(Optional<Character> character) throws LexerException {
    throw new LexerException(LexerException.Type.EOF);
  }

  private LexFunction lexGroup1(char expected, Token.Type tokenType) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
      }
      
      if (character.get() == expected) {
        return this.done(tokenType, character);
      }

      throw new LexerException(LexerException.Type.INVALID_CHARACTER);
    };
  }

  private LexFunction lexGroup2(Token.Type base, Token.Type baseEqual) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        return this.eof(base);
      }
      
      switch (character.get()) {
        case '=':
          return this.done(baseEqual, character);
      }

      return this.forward(base, character);
    };
  }

  private LexFunction lexGroup3(char expected, Token.Type base, Token.Type baseEqual, Token.Type baseDouble) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        return this.eof(base);
      }
      
      if (character.get() == '=') {
        return this.done(baseEqual, character);
      } else if (character.get() == expected) {
        return this.done(baseDouble, character);
      }

      return this.forward(base, character);
    };
  }

  private LexFunction lexZero(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.DECIMAL);
    }

    switch (character.get()) {
      case 'x':
        return this.next(this::lexZeroX, character);
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': 
        return this.next(this::lexDecimal, character);
    }

    return this.forward(Token.Type.DECIMAL, character);
  }

  private LexFunction lexZeroX(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'A': case 'B': case 'C': case 'D': case 'E': 
      case 'F': case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': 
        return this.next(this::lexHexadecimal, character);
    }

    throw new LexerException(LexerException.Type.INVALID_CHARACTER);
  }

  private LexFunction lexDecimal(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.DECIMAL);
    }

    switch (character.get()) {
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': 
        return this.next(this::lexDecimal, character);
    }

    return this.forward(Token.Type.DECIMAL, character);
  }

  private LexFunction lexHexadecimal(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.HEXADECIMAL);
    }

    switch (character.get()) {
      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'A': case 'B': case 'C': case 'D': case 'E': 
      case 'F': case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': 
        return this.next(this::lexHexadecimal, character);
    }

    return this.forward(Token.Type.HEXADECIMAL, character);
  }

  private LexFunction lexCharacter(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '\\':
        return this.next(this::lexCharacterEscape, character);
      case ' ': case '!': case '#': case '$': case '%': case '&': case '(': case ')': case '*': case '+': case ',': 
      case '-': case '.': case '/': case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': 
      case '8': case '9': case ':': case ';': case '<': case '=': case '>': case '?': case '@': case 'A': case 'B': 
      case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': 
      case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': 
      case 'Y': case 'Z': case '[': case ']': case '^': case '_': case '`': case 'a': case 'b': case 'c': case 'd': 
      case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': 
      case 'p': case 'q': case 'r': case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': 
      case '{': case '|': case '}': case '~':
        return this.next(this::lexCharacterClose, character);
    }

    throw new LexerException(LexerException.Type.INVALID_CHARACTER);
  }

  private LexFunction lexCharacterEscape(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '\"': case '\'': case '\\': case 't': case 'n':
        return this.next(this::lexCharacterClose, character);
    }

    throw new LexerException(LexerException.Type.INVALID_ESCAPE);
  }

  private LexFunction lexCharacterClose(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '\'':
        return this.done(Token.Type.CHARACTER, character);
    }

    throw new LexerException(LexerException.Type.INVALID_CHARACTER);
  }

  private LexFunction lexString(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '\\':
        return this.next(this::lexStringEscape, character);
      case '\"':
        return this.done(Token.Type.STRING, character);
      case ' ': case '!': case '#': case '$': case '%': case '&': case '(': case ')': case '*': case '+': case ',': 
      case '-': case '.': case '/': case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': 
      case '8': case '9': case ':': case ';': case '<': case '=': case '>': case '?': case '@': case 'A': case 'B': 
      case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': 
      case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': 
      case 'Y': case 'Z': case '[': case ']': case '^': case '_': case '`': case 'a': case 'b': case 'c': case 'd': 
      case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': 
      case 'p': case 'q': case 'r': case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': 
      case '{': case '|': case '}': case '~':
        return this.next(this::lexString, character);
    }

    throw new LexerException(LexerException.Type.INVALID_CHARACTER);
  }

  private LexFunction lexStringEscape(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '\"': case '\'': case '\\': case 't': case 'n':
        return this.next(this::lexString, character);
    }

    throw new LexerException(LexerException.Type.INVALID_ESCAPE);
  }

  private LexFunction lexSlash(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.SLASH);
    }

    switch (character.get()) {
      case '/':
        return this.next(this::lexSingleLineComment, character);
      case '*':
        return this.next(this::lexMultiLineComment, character);
    }

    return this.forward(Token.Type.SLASH, character);
  }

  private LexFunction lexSingleLineComment(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this::lexEOF;
    }

    switch (character.get()) {
      case '\n':
        return this::lexEmpty;
    }

    return this::lexSingleLineComment;
  }

  private LexFunction lexMultiLineComment(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '*':
        return this::lexMultiLineCommentClose;
    }

    return this::lexMultiLineComment;
  }

  private LexFunction lexMultiLineCommentClose(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(LexerException.Type.UNEXPECTED_EOF);
    }

    switch (character.get()) {
      case '/':
        return this::lexEmpty;
    }

    return this::lexMultiLineComment;
  }

  private LexFunction lexKeyword(String keyword, Token.Type tokenType) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        return this.eof(Token.Type.IDENTIFIER);
      }

      if (keyword.charAt(this.text.length()) == character.get()) {
        this.text.append(character.get());
        if (this.text.length() == keyword.length()) {
          return this.lexKeywordClose(tokenType);
        } else {
          return this.lexKeyword(keyword, tokenType);
        }
      }

      switch (character.get()) {
        case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
        case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
        case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
        case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
        case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
        case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
          return this.next(this::lexIdentifier, character);
      }

      return this.forward(Token.Type.IDENTIFIER, character);
    };
  }

  private LexFunction lexKeywordClose(Token.Type tokenType) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        return this.eof(tokenType);
      }

      switch (character.get()) {
        case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
        case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
        case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
        case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
        case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
        case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
          return this.next(this::lexIdentifier, character);
      }

      return this.forward(tokenType, character);
    };
  }

  private LexFunction lexB(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.IDENTIFIER);
    }

    switch (character.get()) {
      case 'o':
        return this.next(this.lexKeyword("bool", Token.Type.BOOL), character);
      case 'r':
        return this.next(this.lexKeyword("break", Token.Type.BREAK), character);
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
      case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
      case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'p': case 'q': case 's': case 't': 
      case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
        return this.next(this::lexIdentifier, character);
    }

    return this.forward(Token.Type.IDENTIFIER, character);
  }


  private LexFunction lexF(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.IDENTIFIER);
    }

    switch (character.get()) {
      case 'a':
        return this.next(this.lexKeyword("false", Token.Type.FALSE), character);
      case 'o':
        return this.next(this.lexKeyword("for", Token.Type.FOR), character);
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
      case 'X': case 'Y': case 'Z': case '_': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': 
      case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'p': case 'q': case 'r': case 's': case 't': 
      case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
        return this.next(this::lexIdentifier, character);
    }

    return this.forward(Token.Type.IDENTIFIER, character);
  }

  private LexFunction lexI(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.IDENTIFIER);
    }

    switch (character.get()) {
      case 'f':
        return this.next(this.lexKeywordClose(Token.Type.IF), character);
      case 'm':
        return this.next(this.lexKeyword("import", Token.Type.IMPORT), character);
      case 'n':
        return this.next(this.lexKeyword("int", Token.Type.INT), character);
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
      case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'g': case 'h': 
      case 'i': case 'j': case 'k': case 'l': case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u': 
      case 'v': case 'w': case 'x': case 'y': case 'z':
        return this.next(this::lexIdentifier, character);
    }

    return this.forward(Token.Type.IDENTIFIER, character);
  }

  private LexFunction lexIdentifier(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this.eof(Token.Type.IDENTIFIER);
    }

    switch (character.get()) {
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
      case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
      case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
      case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
        return this.next(this::lexIdentifier, character);
    }

    return this.forward(Token.Type.IDENTIFIER, character);
  }

  private LexFunction lexWhitespace(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return this::lexEOF;
    }

    switch (character.get()) {
      case ' ': case '\t': case '\n':
        return this.next(this::lexWhitespace, character);
    }

    return this.lexEmpty(character);
  }

}
