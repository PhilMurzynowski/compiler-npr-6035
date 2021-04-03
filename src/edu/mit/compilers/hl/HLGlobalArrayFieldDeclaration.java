package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLGlobalArrayFieldDeclaration implements HLArrayFieldDeclaration {

  private final VariableType type;
  private final String identifier;
  private final HLIntegerLiteral length;
  private Optional<LLGlobalArrayFieldDeclaration> ll;

  public HLGlobalArrayFieldDeclaration(
    final VariableType type,
    final String identifier,
    final HLIntegerLiteral length)
  {
    this.type = type;
    this.identifier = identifier;
    this.length = length;
    this.ll = Optional.empty();
  }

  public VariableType getType() {
    return type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public HLIntegerLiteral getLength() {
    return length;
  }

  public void setLL(LLGlobalArrayFieldDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  @Override
  public LLGlobalArrayFieldDeclaration getLL() {
    if (ll.isPresent()) {
      return ll.get();
    }
    throw new RuntimeException("ll is empty");
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLGlobalArrayFieldDeclaration {\n");
    s.append(indent(depth+1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth+1) + "type: " + type + ",\n");
    s.append(indent(depth+1) + "length: " + length+ ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth+1) + "ll: " + ll.get().debugString(depth+1) + ",\n");
    }
    s.append("}\n");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
