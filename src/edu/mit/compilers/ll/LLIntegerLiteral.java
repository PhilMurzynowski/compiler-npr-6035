package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLIntegerLiteral implements LLInstruction {

  private final long value;
  private final LLDeclaration result;

  public LLIntegerLiteral(long value, LLDeclaration result) {
    this.value = value;
    this.result = result;
  }

  public long getValue() {
    return this.value;
  }

  public LLDeclaration getResult() {
    return this.result;
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = $" + value;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLIntegerLiteral {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }
}
