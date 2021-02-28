package edu.mit.compilers;

class LexerException extends Exception {

  static final long serialVersionUID = 6239426216427407915L;

  public enum Type {
    INVALID_CHARACTER,
    INVALID_ESCAPE,
    UNEXPECTED_EOF,
    EOF,
  }

  public LexerException(Type type) {
    super(type.toString());
  }

  public LexerException(Type type, String message) {
    super(type.toString() + ": " + message);
  }

}
