package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

// DONE: Noah
public class LLStringLiteralDeclaration implements LLDeclaration {

  private final int index;
  private final String value;

  public LLStringLiteralDeclaration(int index, String value) {
    this.index = index;
    this.value = value;
  }

  public int getIndex() {
    return index;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String location() {
    return "STR" + index;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLStringLiteralDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
