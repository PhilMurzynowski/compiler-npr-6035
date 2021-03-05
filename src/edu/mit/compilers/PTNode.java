package edu.mit.compilers;

import java.util.List;

interface PTNode {

  public List<Token> getTokens();

  public String debugString(int depth);

  @Override
  public String toString();

  @Override
  public boolean equals(Object that);

  @Override
  public int hashCode();

}
