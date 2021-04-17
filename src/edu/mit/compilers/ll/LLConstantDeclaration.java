package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLConstantDeclaration implements LLDeclaration {

  private final long value;

  public LLConstantDeclaration(long value) {
    this.value = value;
  }

  @Override
  public String toUniqueDeclarationString() {
    return "$" + value;
  }

  @Override
  public String prettyString(int depth) {
    return "$" + value;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "declare $" + value;
  }

  @Override
  public String location() {
    return "$" + value;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLConstantDeclaration {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
