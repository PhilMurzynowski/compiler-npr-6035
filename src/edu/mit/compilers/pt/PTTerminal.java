package edu.mit.compilers.pt;

import java.util.Objects;
import java.util.List;

import edu.mit.compilers.common.*;
import edu.mit.compilers.tk.*;

import static edu.mit.compilers.common.Utilities.indent;

public class PTTerminal implements PTNode {

  private final Token token;

  public PTTerminal(Token token) {
    this.token = token;
  }

  @Override
  public TextLocation getTextLocation() {
    return token.getTextLocation();
  }

  @Override
  public boolean is(Token.Type ...tokenTypes) {
    return token.is(tokenTypes);
  }

  @Override
  public boolean is(PTNonterminal.Type ...types) {
    return false;
  }

  @Override
  public String getText() {
    return this.token.getText();
  }

  @Override
  public List<PTNode> getChildren() {
    return List.of();
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
