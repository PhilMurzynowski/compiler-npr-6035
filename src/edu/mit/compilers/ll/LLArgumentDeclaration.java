package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLArgumentDeclaration implements LLScalarFieldDeclaration {

  private final int index;

  public LLArgumentDeclaration(int index) {
    this.index = index;
  }

  @Override
  public String location() {
    if (index == 0) {
      return "%rdi";
    } else if (index == 1) {
      return "%rsi";
    } else if (index == 2) {
      return "%rdx";
    } else if (index == 3) {
      return "%rcx";
    } else if (index == 4) {
      return "%r8";
    } else if (index == 5) {
      return "%r9";
    } else { 
      return (16 + (index - 6) * 8) + "(%rbp)";
    }
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLArgumentDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
