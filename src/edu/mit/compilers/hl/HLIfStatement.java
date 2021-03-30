package edu.mit.compilers.hl;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

public class HLIfStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;
  private final Optional<HLBlock> other;

  public HLIfStatement(HLExpression condition, HLBlock body, Optional<HLBlock> other) {
    this.condition = condition;
    this.body = body;
    this.other = other;
  }

  public HLExpression getCondition() {
    return condition;
  }

  public HLBlock getBody() {
    return body;
  }

  public Optional<HLBlock> getOther() {
    return other;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLIfStatement {\n");
    s.append(indent(depth + 1) + "condition: " + condition.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    if (other.isPresent()) {
      s.append(indent(depth + 1) + "other: " + other.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
