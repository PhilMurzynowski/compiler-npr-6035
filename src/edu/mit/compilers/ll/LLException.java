package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLException implements LLInstruction {

  public enum Type {
    OutOfBounds,
    NoReturnValue,
  }

  private final Type type;

  public LLException(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("except " + type);
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLException {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}