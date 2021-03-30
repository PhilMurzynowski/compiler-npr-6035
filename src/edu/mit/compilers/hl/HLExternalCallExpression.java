package edu.mit.compilers.hl;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class HLExternalCallExpression implements HLCallExpression {

  private final HLImportDeclaration declaration;
  private final List<HLArgument> arguments;

  private HLExternalCallExpression(HLImportDeclaration declaration, List<HLArgument> arguments) {
    this.declaration = declaration;
    this.arguments = arguments;
  }

  public HLImportDeclaration getDeclaration() {
    return declaration;
  }

  public List<HLArgument> getArguments() {
    return arguments;
  }

  public static class Builder {

    private final HLImportDeclaration declaration;
    private final List<HLArgument> arguments;

    public Builder(HLImportDeclaration declaration) {
      this.declaration = declaration;
      arguments = new ArrayList<>();
    }

    public Builder addArgument(HLArgument argument) {
      arguments.add(argument);
      return this;
    }

    public HLExternalCallExpression build() {
      return new HLExternalCallExpression(declaration, List.copyOf(arguments));
    }

  }
  
  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLExternalCallExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "arguments: [\n");
    for (HLArgument argument : arguments) {
      s.append(indent(depth + 2) + argument.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
