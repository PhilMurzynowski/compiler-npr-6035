package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLGlobalScalarFieldDeclaration implements LLScalarFieldDeclaration {

  private final String identifier;

  public LLGlobalScalarFieldDeclaration (String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String location() {
    return identifier;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLGlobalScalarFieldDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
