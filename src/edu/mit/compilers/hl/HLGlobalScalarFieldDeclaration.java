package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLGlobalScalarFieldDeclaration implements HLScalarFieldDeclaration {

  private final VariableType type;
  private final String identifier;
  private Optional<LLGlobalScalarFieldDeclaration> ll;

  public HLGlobalScalarFieldDeclaration(
    final VariableType type,
    final String identifier)
  {
    this.type = type;
    this.identifier = identifier;
  }

  public VariableType getType() {
    return type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setLL(LLGlobalScalarFieldDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  @Override
  public LLGlobalScalarFieldDeclaration getLL() {
    if (ll.isEmpty()) {
      throw new RuntimeException("ll has not been set");
    } else {
      return ll.get();
    }
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLGlobalScalarFieldDeclaration {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
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
