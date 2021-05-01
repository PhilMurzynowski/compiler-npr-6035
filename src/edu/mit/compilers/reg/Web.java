package edu.mit.compilers.reg;

import java.util.*;

import static edu.mit.compilers.common.Utilities.indent;

public class Web {

  private static long counter = 0;

  private final long index;
  private Set<Web> interference;

  public Web() {
    this.index = counter++;
    this.interference = new HashSet<>();
  }

  public void addInterference(Web that) {
    interference.add(that);
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("Web {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "interference: {\n");
    for (Web interferingWeb : interference) {
      s.append(indent(depth + 2) + interferingWeb.index + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }


  @Override
  public String toString() {
    return debugString(0);
  }

}
