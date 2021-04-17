package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

public interface LLInstruction extends LLNode {

  public List<LLDeclaration> uses();
  public Optional<LLDeclaration> definition();
  public LLInstruction usesReplaced(List<LLDeclaration> uses);
  public String getUniqueExpressionString();

}

