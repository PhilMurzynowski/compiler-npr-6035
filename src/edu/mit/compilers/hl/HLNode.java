package edu.mit.compilers.hl;

public interface HLNode { 

  public String debugString(int depth);

  @Override
  public String toString();

}
