package edu.mit.compilers.ll;

import java.util.*; 
import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLExternalCall implements LLInstruction {
  
  private final LLImportDeclaration declaration;
  private final List<LLDeclaration> arguments;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;

  private LLExternalCall(
    final LLImportDeclaration declaration,
    final List<LLDeclaration> arguments,
    final LLDeclaration result)
  {
    this.declaration = declaration;
    this.arguments = arguments;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public static class Builder {

    private final LLImportDeclaration declaration;
    private final List<LLDeclaration> arguments;
    private final LLDeclaration result;

    public Builder(LLImportDeclaration declaration, LLDeclaration result) {
      this.declaration = declaration;
      arguments = new ArrayList<>();
      this.result = result;
    }

    public Builder addArgument(LLDeclaration argument) {
      arguments.add(argument);
      return this;
    }

    public LLExternalCall build() {
      return new LLExternalCall(declaration, List.copyOf(arguments), result);
    }

  }

  public LLImportDeclaration getDeclaration() {
    return declaration;
  }

  public List<LLDeclaration> getArguments() {
    return arguments;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public void setDefinitionWeb(final Web web) {
    if (definitionWeb.isPresent()) {
      throw new RuntimeException("definitionWeb has already been set");
    } else {
      definitionWeb = Optional.of(web);
    }
  }

  @Override
  public void addUsesWeb(final LLDeclaration definition, final Web web) {
    usesWebs.put(definition, web);
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
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLExternalCall((LLImportDeclaration)uses.get(0), uses.subList(1, uses.size()), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
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
    s.append("LLExternalCallExpression {\n");
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

  // private boolean sameValue(LLExternalCall that) {
  //   return declaration.equals(that.declaration)
  //     && arguments.equals(that.arguments)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLExternalCall && sameValue((LLExternalCall)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(declaration, arguments, result);
  // }

}
