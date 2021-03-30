package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class HLArgumentDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;
  private Optional<LLArgumentDeclaration> ll;

  public HLArgumentDeclaration(
    final VariableType type,
    final int index)
  {
    throw new RuntimeException("not implemented");
  }

  public void setLL(LLArgumentDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  @Override
  public LLArgumentDeclaration getLL() {
    if (ll.isPresent()) {
      return ll.get();
    }
    throw new RuntimeException("ll is empty");
  }

  @Override
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
