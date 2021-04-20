package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;
import static edu.mit.compilers.common.Utilities.indent;

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
    this.ll = Optional.empty();
  }

  public int getIndex() {
    return this.index;
  }

  public void setLL(LLLocalScalarFieldDeclaration ll) {
    // if (this.ll.isPresent()) {
    //   throw new RuntimeException("ll has already been set");
    // } else {
      this.ll = Optional.of(ll);
    // }
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
    StringBuilder s = new StringBuilder();
    s.append("HLLocalScalarFieldDeclaration {\n");
    s.append(indent(depth+1) + "type: " + type + ",\n");
    s.append(indent(depth+1) + "index: " + index+ ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth+1) + "ll: " + ll.get().debugString(depth+1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
