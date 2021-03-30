package edu.mit.compilers.hl;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

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
    StringBuilder s = new StringBuilder();
    s.append(indent(depth) + "LLReturnStatement {\n");
    if (expression.isPresent()) {
      s.append(indent(depth + 1) + "expression: " + expression.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
