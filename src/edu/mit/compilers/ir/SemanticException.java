package edu.mit.compilers.ir;

import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class SemanticException extends Exception {

  static final long serialVersionUID = 0L;

  public enum Type {
    DUPLICATE_IDENTIFIER,
    TYPE_MISMATCH,
    INVALID_KEYWORD,
    UNDEFINED_IDENTIFIER,
  }

  private final Type type;
  private final String message;

  public SemanticException(Type type, String message) {
    super(type + ": " + message);
    this.type = type;
    this.message = message;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("SemanticException {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "message: \"" + message + "\",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  public boolean equals(SemanticException that) {
    return (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof SemanticException) && equals((SemanticException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, message);
  }

}
