package edu.mit.compilers.hl;

interface HLNode { 

  public String debugString(int depth);

  @Override
  public String toString();

}
