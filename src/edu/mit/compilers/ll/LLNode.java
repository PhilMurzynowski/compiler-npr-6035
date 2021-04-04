package edu.mit.compilers.ll;

public interface LLNode {

  public String prettyString(int depth);

  public String debugString(int depth);

  @Override
  public String toString();

}
