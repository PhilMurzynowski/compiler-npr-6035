package edu.mit.compilers.hl;

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
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
