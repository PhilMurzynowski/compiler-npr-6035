package edu.mit.compilers;

import java.util.List;

interface PTNode {

  public static PTNode eos() {
    return new PTNonterminal.Builder(PTNonterminal.Type.EOS).build();
  }

  public boolean is(Token.Type ...tokenTypes);

  public boolean is(PTNonterminal.Type ...nonterminalTypes);

  public String getText();

  public List<PTNode> getChildren();

  public String debugString(int depth);

  @Override
  public String toString();

  @Override
  public boolean equals(Object that);

  @Override
  public int hashCode();

}