package edu.mit.compilers;

import java.util.Objects;

class ParserException extends Exception {

  static final long serialVersionUID = 602699916434554042L;

  public enum Type {

    INVALID_TOKEN,
    INCOMPLETE_PARSE,

  }

  private final Type type;

  public ParserException(Type type) {
    super(type.toString());
    this.type = type;
  }

  @Override
  public String toString() {
    return "ParserException {"
      + " type: " + type + ","
      + " }";
  }

  public boolean equals(ParserException that) {
    return type.equals(that.type);
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof ParserException) && equals((ParserException)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

}
