package edu.mit.compilers;

import java.util.Objects;
import java.util.List;

class PTTerminal implements PTNode {

  private final Token token;

  public PTTerminal(Token token) {
    this.token = token;
  }

  @Override
  public List<Token> getTokens() {
    return List.of(token);
  }

  @Override
  public String toString() {
    return "PTTerminal {"
      + " token: " + token + ","
      + " }";
  }

  public boolean equals(PTTerminal that) {
    return token.equals(that.token);
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof PTTerminal) && equals((PTTerminal)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }

};
