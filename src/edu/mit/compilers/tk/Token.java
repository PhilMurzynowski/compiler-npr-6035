package edu.mit.compilers.tk;

import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class Token {

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
    IDENTIFIER,          // [a-zA-Z_][a-zA-Z0-9_]*
    EOF;                 // 

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

  public boolean is(Type ...types) {
    for (Type type : types) {
      if (this.type == type) {
        return true;
      }
    }
    return false;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("Token {\n");
    s.append(indent(depth + 1) + "line: " + line + ",\n");
    s.append(indent(depth + 1) + "column: " + column + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "text: \"" + text + "\",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
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
