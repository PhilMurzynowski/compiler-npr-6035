package edu.mit.compilers.ir;

import java.util.Objects;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class SemanticException extends Exception {

  static final long serialVersionUID = 0L;

  public enum Type {
    DUPLICATE_IDENTIFIER,
    TYPE_MISMATCH,
    INVALID_KEYWORD,
    UNDEFINED_IDENTIFIER,
    UNDEFINED_MAIN,
    MISSING_SYMBOL,
    INCOMPATIBLE_ARGUMENTS,
  }

  private final TextLocation textLocation;
  private final Type type;
  private final String message;

  public SemanticException(TextLocation textLocation, Type type, String message) {
    super(textLocation + ":" + type + ": " + message);
    this.textLocation = textLocation;
    this.type = type;
    this.message = message;
  }

  public TextLocation getTextLocation() {
    return textLocation;
  }

  public Type getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("SemanticException {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
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
    return (textLocation == that.textLocation)
      && (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof SemanticException) && equals((SemanticException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(textLocation, type, message);
  }

}
