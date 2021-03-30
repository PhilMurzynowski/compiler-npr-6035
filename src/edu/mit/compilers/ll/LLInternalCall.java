package edu.mit.compilers.ll;

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
