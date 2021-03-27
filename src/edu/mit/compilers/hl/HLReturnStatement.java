package edu.mit.compilers.hl;

import java.util.Optional;

public class HLReturnStatement implements HLStatement {

  private final Optional<HLExpression> expression;

  public HLReturnStatement() {
    throw new UnsupportedOperationException("not implemented");
  }

}
