package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadArray implements LLInstruction {

  private final LLArrayFieldDeclaration location;
  private final LLDeclaration index;
  private final LLDeclaration result;

  public LLLoadArray(LLArrayFieldDeclaration location, LLDeclaration index, LLDeclaration result) {
    this.location = location;
    this.index = index;
    this.result = result;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLoadArray {\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
