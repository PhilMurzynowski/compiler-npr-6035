package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLCopy implements LLInstruction {

  private final LLDeclaration input;
  private final LLDeclaration result;

  public LLCopy(LLDeclaration input, LLDeclaration result) {
    this.input = input;
    this.result = result;
  }

  public LLDeclaration getInput() {
    return input;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(input);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLCopy(uses.get(0), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = " + input.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLCopy {\n");
    s.append(indent(depth + 1) + "input: " + input.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  private boolean sameValue(LLCopy that) {
    return input.equals(that.input)
      && result.equals(that.result);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLCopy && sameValue((LLCopy)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(input, result);
  }

}
