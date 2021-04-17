package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLGlobalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final String identifier;
  private final long length;

  public LLGlobalArrayFieldDeclaration(String identifier, long length) {
    this.identifier = identifier;
    this.length = length;
  }

  @Override
  public long getLength() {
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
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need array declaration as string");
  }

  @Override
  public String prettyString(int depth) {
    return "@" + identifier;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "@" + identifier + " = [" + length + " x i64] { 0 }";
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
