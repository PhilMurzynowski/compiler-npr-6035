package edu.mit.compilers;

import java.util.List;

interface PTNode {

  public boolean is(Token.Type tokenType);

  public boolean in(Token.Type ...tokenTypes);

  public boolean is(PTNonterminal.Type nonterminalType);

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
