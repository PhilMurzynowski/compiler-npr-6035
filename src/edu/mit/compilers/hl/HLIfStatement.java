package edu.mit.compilers.hl;

import java.util.Optional;

public class HLIfStatement implements HLStatement {

  private final HLExpression condition;
  private final HLBlock body;
  private final Optional<HLBlock> other;

  public HLIfStatement(HLExpression condition, HLBlock body, Optional<HLBlock> other) {
    throw new UnsupportedOperationException("not implemented");
  }

}
