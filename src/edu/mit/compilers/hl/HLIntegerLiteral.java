package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLIntegerLiteral implements HLExpression {

  private final long value;

  public HLIntegerLiteral(long value) {
    this.value = value;
  }

  public long getValue() {
    return this.value;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLIntegerLiteral {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
