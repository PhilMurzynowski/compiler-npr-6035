package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

// TODO: Noah (debugString)
public class HLWhileStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;

  public HLWhileStatement(HLExpression condition, HLBlock body) {
    this.condition = condition;
    this.body = body;
  }

  public HLExpression getCondition() {
    return condition;
  }

  public HLBlock getBody() {
    return body;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLWhileStatement {\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
