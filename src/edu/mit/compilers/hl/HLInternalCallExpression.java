package edu.mit.compilers.hl;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class HLInternalCallExpression implements HLCallExpression {

  private final HLMethodDeclaration declaration;
  private final List<HLArgument> arguments;
  private boolean inline;

  private HLInternalCallExpression(HLMethodDeclaration declaration, List<HLArgument> arguments) {
    this.declaration = declaration;
    this.arguments = arguments;
    this.inline = false;
  }

  public static class Builder {

    private final HLMethodDeclaration declaration;
    private final List<HLArgument> arguments;

    public Builder(HLMethodDeclaration declaration) {
      this.declaration = declaration;
      arguments = new ArrayList<>();
    }

    public Builder addArgument(HLArgument argument) {
      arguments.add(argument);
      return this;
    }

    public HLInternalCallExpression build() {
      return new HLInternalCallExpression(declaration, List.copyOf(arguments));
    }

  }

  public HLMethodDeclaration getDeclaration() {
    return declaration;
  }

  public List<HLArgument> getArguments() {
    return arguments;
  }

  public void setInline() {
    inline = true;
  }

  public boolean shouldInline() {
    return inline;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLInternalCallExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.getIdentifier() + ",\n");
    s.append(indent(depth + 1) + "arguments: [\n");
    for (HLArgument argument : arguments) {
      s.append(indent(depth + 2) + argument.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "inline: " + inline + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
