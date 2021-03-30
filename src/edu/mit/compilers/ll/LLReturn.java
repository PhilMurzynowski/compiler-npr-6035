package edu.mit.compilers.ll;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

public class LLReturn implements LLInstruction {

  private final Optional<LLDeclaration> expression;

  public LLReturn(Optional<LLDeclaration> expression) {
    this.expression = expression;
  }

  public Optional<LLDeclaration> getExpression() {
    return expression;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLReturn {\n");
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
