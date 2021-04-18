package edu.mit.compilers.ll;

import java.util.Objects;

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
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need string declaration as string");
  }

  @Override
  public String prettyString(int depth) {
    return "@STR" + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "@STR" + index + " = \"" + value + "\"";
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

  private boolean sameValue(LLStringLiteralDeclaration that) {
    return index == that.index
      && value.equals(that.value);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLStringLiteralDeclaration && sameValue((LLStringLiteralDeclaration)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, value);
  }

}
