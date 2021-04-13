package edu.mit.compilers.ll;

import java.util.Set;

public interface LLInstruction extends LLNode {

  public Set<LLDeclaration> uses();
  public Set<LLDeclaration> definition();

}

