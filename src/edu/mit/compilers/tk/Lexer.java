package edu.mit.compilers.tk;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import edu.mit.compilers.common.*;

public class Lexer {

  private final List<Token> tokens;
  private final StringBuilder text;
  private TextLocation currentTextLocation;
  private TextLocation startTextLocation;
  private final List<LexerException> exceptions;

  public Lexer() {
    tokens = new ArrayList<>();
    text = new StringBuilder();
    exceptions = new ArrayList<>();
  }

  public Result lexAll(String input) {
    clear();

    LexFunction lexFunction = this::lexEmpty;
    for (char c : input.toCharArray()) {
      try {
        lexFunction = lexFunction.apply(Optional.of(c));
      } catch (LexerException exception) {
        exceptions.add(exception);
        consume(Optional.of(c));
        lexFunction = reset();
      }
    }

    try {
      lexFunction.apply(Optional.empty());
    } catch (LexerException exception) {
      exceptions.add(exception);
      accept();
    }

    produce(Token.Type.EOF);

    return new Result(tokens, exceptions);
  }

  private LexFunction lexLogical(char c, Token.Type type) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected '" + c + "'");
      } else if (character.get() == c) {
        consume(character);
        produce(type);
        return reset();
      } else {
        throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "expected '" + c + "'");
      }
    };
  }

  private LexFunction lexComparison(Token.Type base, Token.Type baseEqual) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        produce(base);
        return accept();
      } else if (character.get() == '=') {
        consume(character);
        produce(baseEqual);
        return reset();
      } else {
        produce(base);
        return redo(character);
      }
    };
  }

  private LexFunction lexPlusMinus(char c, Token.Type base, Token.Type baseEqual, Token.Type baseBase) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        produce(base);
        return accept();
      } else if (character.get() == '=') {
        consume(character);
        produce(baseEqual);
        return reset();
      } else if (character.get() == c) {
        consume(character);
        produce(baseBase);
        return reset();
      } else {
        produce(base);
        return redo(character);
      }
    };
  }

  private LexFunction lexZero(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.DECIMAL);
      return accept();
    } else if (character.get() == 'x') {
      consume(character);
      return next(this::lexZeroX);
    } else if (isDecimal(character.get())) {
      consume(character);
      return next(this::lexDecimal);
    } else {
      produce(Token.Type.DECIMAL);
      return redo(character);
    }
  }

  private LexFunction lexZeroX(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected [0-9A-Fa-f]");
    } else if (isHexadecimal(character.get())) {
      consume(character);
      return next(this::lexHexadecimal);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "expected [0-9A-Fa-f]");
    }
  }

  private LexFunction lexDecimal(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.DECIMAL);
      return accept();
    } else if (isDecimal(character.get())) {
      consume(character);
      return next(this::lexDecimal);
    } else {
      produce(Token.Type.DECIMAL);
      return redo(character);
    }
  }

  private LexFunction lexHexadecimal(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.HEXADECIMAL);
      return accept();
    } else if (isHexadecimal(character.get())) {
      consume(character);
      return next(this::lexHexadecimal);
    } else {
      produce(Token.Type.HEXADECIMAL);
      return redo(character);
    }
  }

  private LexFunction lexCharacter(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected [\\x20-\\x7E&&[^\"']]");
    } else if (character.get() == '\\') {
      consume(character);
      return next(this::lexCharacterEscape);
    } else if (isUnescaped(character.get())) {
      consume(character);
      return next(this::lexCharacterClose);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "expected [\\x20-\\x7E&&[^\"']]");
    }
  }

  private LexFunction lexCharacterEscape(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected [\"'\\tn]");
    } else if (isEscaped(character.get())) {
      consume(character);
      return next(this::lexCharacterClose);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_ESCAPE, "expected [\"'\\tn]");
    }
  }

  private LexFunction lexCharacterClose(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected '\\''");
    } else if (character.get() == '\'') {
      consume(character);
      produce(Token.Type.CHARACTER);
      return reset();
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "expected '\\''");
    }
  }

  private LexFunction lexString(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "no matching '\"'");
    } else if (character.get() == '\\') {
      consume(character);
      return next(this::lexStringEscape);
    } else if (character.get() == '\"') {
      consume(character);
      produce(Token.Type.STRING);
      return reset();
    } else if (isUnescaped(character.get())) {
      consume(character);
      return next(this::lexString);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "expected [\\x20-\\x7E&&[^']]");
    }
  }

  private LexFunction lexStringEscape(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "expected [\"'\\tn]");
    } else if (isEscaped(character.get())) {
      consume(character);
      return next(this::lexString);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_ESCAPE, "expected [\"'\\tn]");
    }
  }

  private LexFunction lexSlash(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.SLASH);
      return accept();
    } else if (character.get() == '/') {
      consume(character);
      return next(this::lexSingleLineComment);
    } else if (character.get() == '*') {
      consume(character);
      return next(this::lexMultiLineComment);
    } else {
      produce(Token.Type.SLASH);
      return redo(character);
    }
  }

  private LexFunction lexSingleLineComment(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return accept();
    } else if (character.get() == '\n') {
      consume(character);
      return reset();
    } else {
      consume(character);
      return next(this::lexSingleLineComment);
    }
  }

  private LexFunction lexMultiLineComment(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "no matching \"*/\"");
    } else if (character.get() == '*') {
      consume(character);
      return next(this::lexMultiLineCommentClose);
    } else {
      consume(character);
      return next(this::lexMultiLineComment);
    }
  }

  private LexFunction lexMultiLineCommentClose(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      throw new LexerException(currentTextLocation, LexerException.Type.UNEXPECTED_EOF, "no matching \"*/\"");
    } else if (character.get() == '/') {
      consume(character);
      return reset();
    } else if (character.get() == '*') {
      consume(character);
      return next(this::lexMultiLineCommentClose);
    } else {
      consume(character);
      return next(this::lexMultiLineComment);
    }
  }

  private LexFunction lexKeyword(String keyword, Token.Type type) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        produce(Token.Type.IDENTIFIER);
        return accept();
      } else if (keyword.charAt(text.length()) == character.get()) {
        if (text.length() + 1 == keyword.length()) {
          consume(character);
          return next(lexKeywordClose(type));
        } else {
          consume(character);
          return next(lexKeyword(keyword, type));
        }
      } else if (isIdentifierTail(character.get())) {
        consume(character);
        return next(this::lexIdentifier);
      } else {
        produce(Token.Type.IDENTIFIER);
        return redo(character);
      }
    };
  }

  private LexFunction lexKeywordClose(Token.Type type) {
    return (Optional<Character> character) -> {
      if (!character.isPresent()) {
        produce(type);
        return accept();
      } else if (isIdentifierTail(character.get())) {
        consume(character);
        return next(this::lexIdentifier);
      } else {
        produce(type);
        return redo(character);
      }
    };
  }

  private LexFunction lexB(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.IDENTIFIER);
      return accept();
    } else if (character.get() == 'o') {
      consume(character);
      return next(lexKeyword("bool", Token.Type.BOOL));
    } else if (character.get() == 'r') {
      consume(character);
      return next(lexKeyword("break", Token.Type.BREAK));
    } else if (isIdentifierTail(character.get())) {
      consume(character);
      return next(this::lexIdentifier);
    } else {
      produce(Token.Type.IDENTIFIER);
      return redo(character);
    }
  }

  private LexFunction lexF(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.IDENTIFIER);
      return accept();
    } else if (character.get() == 'a') {
      consume(character);
      return next(lexKeyword("false", Token.Type.FALSE));
    } else if (character.get() == 'o') {
      consume(character);
      return next(lexKeyword("for", Token.Type.FOR));
    } else if (isIdentifierTail(character.get())) {
      consume(character);
      return next(this::lexIdentifier);
    } else {
      produce(Token.Type.IDENTIFIER);
      return redo(character);
    }
  }

  private LexFunction lexI(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.IDENTIFIER);
      return accept();
    } else if (character.get() == 'f') {
      consume(character);
      return next(lexKeywordClose(Token.Type.IF));
    } else if (character.get() == 'm') {
      consume(character);
      return next(lexKeyword("import", Token.Type.IMPORT));
    } else if (character.get() == 'n') {
      consume(character);
      return next(lexKeyword("int", Token.Type.INT));
    } else if (isIdentifierTail(character.get())) {
      consume(character);
      return next(this::lexIdentifier);
    } else {
      produce(Token.Type.IDENTIFIER);
      return redo(character);
    }
  }

  private LexFunction lexIdentifier(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      produce(Token.Type.IDENTIFIER);
      return accept();
    } else if (isIdentifierTail(character.get())) {
      consume(character);
      return next(this::lexIdentifier);
    } else {
      produce(Token.Type.IDENTIFIER);
      return redo(character);
    }
  }

  private LexFunction lexWhitespace(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return accept();
    } else if (isWhitespace(character.get())) {
      consume(character);
      return next(this::lexWhitespace);
    } else {
      return redo(character);
    }
  }

  private LexFunction lexEOF(Optional<Character> character) throws LexerException {
    throw new RuntimeException("unreachable");
  }

  private LexFunction lexEmpty(Optional<Character> character) throws LexerException {
    if (!character.isPresent()) {
      return accept();
    } else if (character.get() == ';') {
      consume(character);
      produce(Token.Type.SEMICOLON);
      return reset();
    } else if (character.get() == '[') {
      consume(character);
      produce(Token.Type.LEFT_SQUARE);
      return reset();
    } else if (character.get() == ']') {
      consume(character);
      produce(Token.Type.RIGHT_SQUARE);
      return reset();
    } else if (character.get() == ',') {
      consume(character);
      produce(Token.Type.COMMA);
      return reset();
    } else if (character.get() == '(') {
      consume(character);
      produce(Token.Type.LEFT_ROUND);
      return reset();
    } else if (character.get() == ')') {
      consume(character);
      produce(Token.Type.RIGHT_ROUND);
      return reset();
    } else if (character.get() == '{') {
      consume(character);
      produce(Token.Type.LEFT_CURLY);
      return reset();
    } else if (character.get() == '}') {
      consume(character);
      produce(Token.Type.RIGHT_CURLY);
      return reset();
    } else if (character.get() == '%') {
      consume(character);
      produce(Token.Type.PERCENT);
      return reset();
    } else if (character.get() == '*') {
      consume(character);
      produce(Token.Type.STAR);
      return reset();
    } else if (character.get() == '&') {
      consume(character);
      return next(lexLogical('&', Token.Type.AMPERSAND_AMPERSAND));
    } else if (character.get() == '|') {
      consume(character);
      return next(lexLogical('|', Token.Type.VERTICAL_VERTICAL));
    } else if (character.get() == '=') {
      consume(character);
      return next(lexComparison(Token.Type.EQUAL, Token.Type.EQUAL_EQUAL));
    } else if (character.get() == '!') {
      consume(character);
      return next(lexComparison(Token.Type.BANG, Token.Type.BANG_EQUAL));
    } else if (character.get() == '<') {
      consume(character);
      return next(lexComparison(Token.Type.LESS, Token.Type.LESS_EQUAL));
    } else if (character.get() == '>') {
      consume(character);
      return next(lexComparison(Token.Type.GREATER, Token.Type.GREATER_EQUAL));
    } else if (character.get() == '+') {
      consume(character);
      return next(lexPlusMinus('+', Token.Type.PLUS, Token.Type.PLUS_EQUAL, Token.Type.PLUS_PLUS));
    } else if (character.get() == '-') {
      consume(character);
      return next(lexPlusMinus('-', Token.Type.MINUS, Token.Type.MINUS_EQUAL, Token.Type.MINUS_MINUS));
    } else if (character.get() == '0') {
      consume(character);
      return next(this::lexZero);
    } else if (isDecimal(character.get())) {
      consume(character);
      return next(this::lexDecimal);
    } else if (character.get() == '\'') {
      consume(character);
      return next(this::lexCharacter);
    } else if (character.get() == '\"') {
      consume(character);
      return next(this::lexString);
    } else if (character.get() == '/') {
      consume(character);
      return next(this::lexSlash);
    } else if (character.get() == 'b') {
      consume(character);
      return next(this::lexB);
    } else if (character.get() == 'c') {
      consume(character);
      return next(lexKeyword("continue", Token.Type.CONTINUE));
    } else if (character.get() == 'e') {
      consume(character);
      return next(lexKeyword("else", Token.Type.ELSE));
    } else if (character.get() == 'f') {
      consume(character);
      return next(this::lexF);
    } else if (character.get() == 'i') {
      consume(character);
      return next(this::lexI);
    } else if (character.get() == 'l') {
      consume(character);
      return next(lexKeyword("len", Token.Type.LEN));
    } else if (character.get() == 'r') {
      consume(character);
      return next(lexKeyword("return", Token.Type.RETURN));
    } else if (character.get() == 't') {
      consume(character);
      return next(lexKeyword("true", Token.Type.TRUE));
    } else if (character.get() == 'v') {
      consume(character);
      return next(lexKeyword("void", Token.Type.VOID));
    } else if (character.get() == 'w') {
      consume(character);
      return next(lexKeyword("while", Token.Type.WHILE));
    } else if (isIdentifierHead(character.get())) {
      consume(character);
      return next(this::lexIdentifier);
    } else if (isWhitespace(character.get())) {
      consume(character);
      return next(this::lexWhitespace);
    } else {
      throw new LexerException(currentTextLocation, LexerException.Type.INVALID_CHARACTER, "invalid character");
    }
  }

  private interface LexFunction {
    public LexFunction apply(Optional<Character> character) throws LexerException;
  }
  
  private void clear() {
    tokens.clear();
    text.setLength(0);
    currentTextLocation = TextLocation.start();
    startTextLocation = currentTextLocation;
    exceptions.clear();
  }

  private void consume(Optional<Character> character) {
    if (character.get() == '\n') {
      currentTextLocation = currentTextLocation.incrementLine();
    } else {
      currentTextLocation = currentTextLocation.incrementColumn();
    }
    text.append(character.get());
  }

  private void produce(Token.Type tokenType) {
    tokens.add(new Token(startTextLocation, tokenType, text.toString()));
  }

  private LexFunction reset() {
    text.setLength(0);
    startTextLocation = currentTextLocation;
    return this::lexEmpty;
  }

  private LexFunction redo(Optional<Character> character) throws LexerException {
    text.setLength(0);
    startTextLocation = currentTextLocation;
    return lexEmpty(character);
  }

  private LexFunction next(LexFunction lexFunction) {
    return lexFunction;
  }

  private LexFunction accept() {
    text.setLength(0);
    startTextLocation = currentTextLocation;
    return this::lexEOF;
  }

  private boolean isDecimal(char c) {
    switch (c) {
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
        return true;
      default:
        return false;
    }
  }

  private boolean isHexadecimal(char c) {
    switch (c) {
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
        return true;
      default:
        return false;
    }
  }

  private boolean isIdentifierHead(char c) {
    switch (c) {
      case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': 
      case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': 
      case 'W': case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': 
      case 'g': case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': 
      case 'r': case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
        return true;
      default:
        return false;
    }
  }

  private boolean isIdentifierTail(char c) {
    switch (c) {
      case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'A': 
      case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': 
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': 
      case 'X': case 'Y': case 'Z': case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
      case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': 
      case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
        return true;
      default:
        return false;
    }
  }
  
  private boolean isWhitespace(char c) {
    switch (c) {
      case ' ': case '\t': case '\n':
        return true;
      default:
        return false;
    }
  }

  private boolean isUnescaped(char c) {
    switch (c) {
      case ' ': case '!': case '#': case '$': case '%': case '&': case '(': case ')': case '*': case '+': case ',': 
      case '-': case '.': case '/': case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': 
      case '8': case '9': case ':': case ';': case '<': case '=': case '>': case '?': case '@': case 'A': case 'B': 
      case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M':
      case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': 
      case 'Y': case 'Z': case '[': case ']': case '^': case '_': case '`': case 'a': case 'b': case 'c': case 'd': 
      case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': 
      case 'p': case 'q': case 'r': case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': 
      case '{': case '|': case '}': case '~':
        return true;
      default:
        return false;
    }
  }

  private boolean isEscaped(char c) {
    switch (c) {
      case '\"': case '\'': case '\\': case 't': case 'n':
        return true;
      default:
        return false;
    }
  }

  public static class Result {

    private final List<Token> tokens;
    private final List<LexerException> exceptions;

    private Result(List<Token> tokens, List<LexerException> exceptions) {
      this.tokens = List.copyOf(tokens);
      this.exceptions = List.copyOf(exceptions);
    }

    public List<Token> getTokens() {
      return tokens;
    }

    public boolean hasExceptions() {
      return !exceptions.isEmpty();
    }

    public List<LexerException> getExceptions() {
      return exceptions;
    }

  }

}
