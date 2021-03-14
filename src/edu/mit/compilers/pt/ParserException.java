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

  private final Location location;
  private final Type type;
  private final String message;

  public ParserException(Token token, Type type, String message) {
    super(token.getLocation() + ":" + type + ": " + message);
    this.location = token.getLocation();
    this.type = type;
    this.message = message;
  }

  public Location getLocation() {
    return location;
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
    s.append(indent(depth + 1) + "line: " + location.debugString(depth + 1) + ",\n");
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
    return (location == that.location)
      && (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof ParserException) && equals((ParserException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, type, message);
  }

}
