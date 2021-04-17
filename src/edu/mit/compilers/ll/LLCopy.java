package edu.mit.compilers.ll;

import edu.mit.compilers.common.*;

import java.util.Optional;
import java.util.List;

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
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = ");
    s.append(" " + input.prettyString(depth));

    return s.toString();
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

}
