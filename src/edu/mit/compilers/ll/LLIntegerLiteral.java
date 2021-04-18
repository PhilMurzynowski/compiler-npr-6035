package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLIntegerLiteral implements LLInstruction {

  private final long value;
  private final LLDeclaration result;

  public LLIntegerLiteral(long value, LLDeclaration result) {
    this.value = value;
    this.result = result;
  }

  public long getValue() {
    return this.value;
  }

  public LLDeclaration getResult() {
    return this.result;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of();
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return this;
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = $" + value;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLIntegerLiteral {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  private boolean sameValue(LLIntegerLiteral that) {
    return value == that.value
      && result.equals(that.result);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLIntegerLiteral && sameValue((LLIntegerLiteral)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, result);
  }

}
