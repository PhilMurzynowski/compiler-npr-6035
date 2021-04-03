package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

public class HLLocalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;
  private Optional<LLLocalScalarFieldDeclaration> ll;

  public HLLocalScalarFieldDeclaration(
    final VariableType type,
    final int index)
  {
    this.type = type;
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public void setLL(LLLocalScalarFieldDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  @Override
  public LLLocalScalarFieldDeclaration getLL() {
    if (this.ll.isPresent()) {
      return this.ll.get();
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
