package edu.mit.compilers;

import java.util.Objects;

class Token {

  public enum Type {

    SEMICOLON,           // ;
    LEFT_SQUARE,         // [
    RIGHT_SQUARE,        // ]
    COMMA,               // ,
    LEFT_ROUND,          // (
    RIGHT_ROUND,         // )
    LEFT_CURLY,          // }
    RIGHT_CURLY,         // {
    PERCENT,             // %
    STAR,                // *
    AMPERSAND_AMPERSAND, // &&
    VERTICAL_VERTICAL,   // ||
    EQUAL_EQUAL,         // ==
    EQUAL,               // =
    PLUS_EQUAL,          // +=
    PLUS_PLUS,           // ++
    PLUS,                // +
    MINUS_EQUAL,         // -=
    MINUS_MINUS,         // --
    MINUS,               // -
    BANG_EQUAL,          // !=
    BANG,                // !
    LESS_EQUAL,          // <=
    LESS,                // <
    GREATER_EQUAL,       // >=
    GREATER,             // >
    HEXADECIMAL,         // 0x[a-zA-Z0-9]+
    DECIMAL,             // [0-9]+
    CHARACTER,           // '.'
    STRING,              // ".*"
    SLASH,               // /
    BOOL,                // bool
    BREAK,               // break
    CONTINUE,            // continue
    ELSE,                // else
    FALSE,               // false
    FOR,                 // for
    IF,                  // if
    IMPORT,              // import
    INT,                 // int
    LEN,                 // len
    RETURN,              // return
    TRUE,                // true
    VOID,                // void
    WHILE,               // while
    IDENTIFIER;          // [a-zA-Z_][a-zA-Z0-9_]*

  }

  private final int line;
  private final int column;
  private final Type type;
  private final String text;

  public Token(int line, int column, Type type, String text) {
    this.line = line;
    this.column = column;
    this.type = type;
    this.text = text;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public Type getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "Token {"
      + " line: " + line + "," 
      + " column: " + column + ","
      + " type: " + type + ","
      + " text: '" + text + "',"
      + " }";
  }

  public boolean equals(Token that) {
    return (line == that.line)
      && (column == that.column)
      && (type.equals(that.type))
      && (text.equals(that.text));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof Token) && equals((Token)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(column, line, type, text);
  }

}
