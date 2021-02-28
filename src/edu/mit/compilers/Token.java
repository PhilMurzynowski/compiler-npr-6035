package edu.mit.compilers;

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
    LESS_THAN_EQUAL,     // <=
    LESS_THAN,           // <
    GREATER_THAN_EQUAL,  // >=
    GREATER_THAN,        // >
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

  private final Type type;
  private final String text;

  public Token(Type type, String text) {
    this.type = type;
    this.text = text;
  }

  @Override
  public String toString() {
    return "Token { type: " + this.type + ", text: '" + this.text + "' }";
  }

  // TODO(rbd): Implement equals() for immutable type Token

  // TODO(rbd): Implement hashCode() for immutable type Token

}
