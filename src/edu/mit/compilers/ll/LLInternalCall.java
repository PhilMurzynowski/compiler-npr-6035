package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class LLInternalCall implements LLInstruction {

  private final LLMethodDeclaration declaration;
  private final List<LLDeclaration> arguments;
  private final LLDeclaration result;

  public LLInternalCall(LLMethodDeclaration declaration, List<LLDeclaration> arguments, LLDeclaration result) {
    this.declaration = declaration;
    this.arguments = arguments;
    this.result = result;
  }

  public static class Builder {

    private final LLMethodDeclaration declaration;
    private final List<LLDeclaration> arguments;
    private final LLDeclaration result;

    public Builder(LLMethodDeclaration declaration, LLDeclaration result) {
      this.declaration = declaration;
      arguments = new ArrayList<>();
      this.result = result;
    }

    public Builder addArgument(LLDeclaration argument) {
      arguments.add(argument);
      return this;
    }

    public LLInternalCall build() {
      return new LLInternalCall(declaration, List.copyOf(arguments), result);
    }

  }

  public LLMethodDeclaration getDeclaration() {
    return declaration;
  }

  public List<LLDeclaration> getArguments() {
    return arguments;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public List<LLDeclaration> uses() {
    final List<LLDeclaration> uses = new ArrayList<>();

    uses.add(declaration);
    for (LLDeclaration argument : arguments) {
      uses.add(argument);
    }

    return uses;
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = call " + declaration.prettyString(depth) + "(");

    if (arguments.size() > 0) {
      s.append(arguments.get(0).prettyString(depth));

      for (int i = 1; i < arguments.size(); i++) {
        s.append(", " + arguments.get(i).prettyString(depth));
      }
    }

    s.append(")");

    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLInternalCallExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "arguments: [\n");
    for (LLDeclaration argument : arguments) {
      s.append(indent(depth + 2) + argument.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
