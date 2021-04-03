package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

// TODO: Noah (debugString)
public class HLCallStatement implements HLStatement {

  private final HLCallExpression call;

  public HLCallStatement(final HLCallExpression call) {
    this.call = call;
  }

  public HLCallExpression getCall() {
    return call;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLCallStatement {\n");
    s.append(indent(depth + 1) + "call: " + call.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
