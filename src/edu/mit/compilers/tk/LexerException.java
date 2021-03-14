package edu.mit.compilers.tk;

import java.util.Objects;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LexerException extends Exception {

  static final long serialVersionUID = 0L;

  public enum Type {
    INVALID_CHARACTER,
    INVALID_ESCAPE,
    UNEXPECTED_EOF,
  }

  private final Location location;
  private final Type type;
  private final String message;

  public LexerException(Location location, Type type, String message) {
    super(location + ":" + ": " + type + ": " + message);
    this.location = location;
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
    s.append("LexerException {\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
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
    return (location.equals(that.location))
      && (type.equals(that.type))
      && (message.equals(that.message));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof LexerException) && equals((LexerException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, type, message);
  }

}
