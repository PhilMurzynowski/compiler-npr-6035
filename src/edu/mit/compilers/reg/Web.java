package edu.mit.compilers.reg;

import static edu.mit.compilers.common.Utilities.indent;

public class Web {

  private static long counter = 0;

  private final long index;

  public Web() {
    this.index = counter++;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("Web {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
