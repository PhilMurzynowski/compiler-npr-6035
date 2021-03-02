package edu.mit.compilers;

import java.util.List;

interface PTNode {

  public List<Token> getTokens();

  @Override
  public String toString();

  @Override
  public boolean equals(Object that);

  @Override
  public int hashCode();

}
