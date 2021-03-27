package edu.mit.compilers.hl;

import java.util.Optional;

public class HLReturnStatement implements HLStatement {

  private final Optional<HLExpression> expression;

  public HLReturnStatement(Optional<HLExpression> expression) {
    throw new UnsupportedOperationException("not implemented");
  }

}
