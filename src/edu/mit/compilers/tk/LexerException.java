package edu.mit.compilers.tk;

import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LexerException extends Exception {

  static final long serialVersionUID = 0L;

  public enum Type {
    INVALID_CHARACTER,
    INVALID_ESCAPE,
    UNEXPECTED_EOF,
  }

  private final int line;
  private final int column;
  private final Type type;
  private final String message;

  public LexerException(int line, int column, Type type, String message) {
    super(line + ":" + column + ": " + type + ": " + message);
    this.line = line;
    this.column = column;
    this.type = type;
    this.message = message;
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

  public String getMessage() {
    return message;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LexerException {\n");
    s.append(indent(depth + 1) + "line: " + line + ",\n");
    s.append(indent(depth + 1) + "column: " + column + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "message: \"" + message + "\",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  public boolean equals(LexerException that) {
    return (line == that.line)
      && (column == that.column)
      && (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof LexerException) && equals((LexerException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(line, column, type, message);
  }

}
