package edu.mit.compilers.hl;

import java.util.Optional;

public class HLIfStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;
  private final Optional<HLBlock> other;

  public HLIfStatement() {
    throw new UnsupportedOperationException("not implemented");
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
