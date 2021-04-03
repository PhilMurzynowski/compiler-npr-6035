package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

// DONE: Noah
public class HLForStatement implements HLStatement {

  private final HLStoreScalarStatement initial;
  private final HLExpression condition;
  private final HLStoreStatement update;
  private final HLBlock body;

  public HLForStatement(
      HLStoreScalarStatement initial,
      HLExpression condition,
      HLStoreStatement update,
      HLBlock body)
  {
    this.initial = initial;
    this.condition = condition;
    this.update = update;
    this.body = body;
  }

  public HLStoreScalarStatement getInitial() {
    return initial;
  }

  public HLExpression getCondition() {
    return condition;
  }

  public HLStoreStatement getUpdate() {
    return update;
  }

  public HLBlock getBody() {
    return body;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLForStatement {\n");
    s.append(indent(depth + 1) + "initial: " + initial.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "update: " + update.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
