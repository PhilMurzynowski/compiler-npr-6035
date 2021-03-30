package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLGlobalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final String identifier;
  private final int length;

  public LLGlobalArrayFieldDeclaration(String identifier, int length) {
    this.identifier = identifier;
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  @Override
  public String location() {
    return identifier;
  }

  @Override
  public String index(String register) {
    return identifier + "(," + register + ",8)";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLGlobalArrayFieldDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "length: " + length + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
