package edu.mit.compilers.pt;

import java.util.Objects;

import edu.mit.compilers.common.*;
import edu.mit.compilers.tk.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ParserException extends Exception {

  static final long serialVersionUID = 0L;

  public enum Type {
    INVALID_TOKEN,
    INCOMPLETE_PARSE,
    UNEXPECTED_EOF,
  }

  private final TextLocation textLocation;
  private final Type type;
  private final String message;

  public ParserException(Token token, Type type, String message) {
    super(token.getTextLocation() + ":" + type + ": " + message);
    this.textLocation = token.getTextLocation();
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
    s.append("ParserException {\n");
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

  public boolean equals(ParserException that) {
    return (textLocation == that.textLocation)
      && (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof ParserException) && equals((ParserException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(textLocation, type, message);
  }

}
