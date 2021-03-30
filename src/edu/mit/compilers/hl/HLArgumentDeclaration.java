package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLArgumentDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final int index;
  private Optional<LLArgumentDeclaration> ll;

  public HLArgumentDeclaration(
    final VariableType type,
    final int index)
  {
    this.type = type;
    this.index = index;
    ll = Optional.empty();
  }

  public int getIndex() {
    return index;
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
    StringBuilder s = new StringBuilder();
    s.append("HLArgumentDeclaration {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth + 1) + "ll: " + ll.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
