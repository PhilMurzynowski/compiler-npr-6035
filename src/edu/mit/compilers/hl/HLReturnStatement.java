package edu.mit.compilers.hl;

import java.util.Optional;

public class HLReturnStatement implements HLStatement {

  private final Optional<HLExpression> expression;

  public HLReturnStatement(Optional<HLExpression> expression) {
    this.expression = expression;
  }

	public Optional<HLExpression> getExpression() {
		return expression;
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
