package edu.mit.compilers;

import java.util.Objects;
import java.util.List;

import static edu.mit.compilers.Utilities.indent;

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
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("PTTerminal {\n");
    s.append(indent(depth + 1) + "token: " + token.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
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
