package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

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
  public List<LLDeclaration> uses() {
    if (expression.isPresent()) {
      return List.of(expression.get());
    } else {
      return List.of();
    }
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.empty();
  }
  
  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    if (uses.size() > 0) {
      return new LLReturn(Optional.of(uses.get(0)));
    } else {
      return new LLReturn(Optional.empty());
    }
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ret");
    if (expression.isPresent()) {
      s.append(" " + expression.get().prettyString(depth));
    }
    return s.toString();
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

  private boolean sameValue(LLReturn that) {
    return expression.equals(that.expression);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLReturn && sameValue((LLReturn)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expression);
  }

}
