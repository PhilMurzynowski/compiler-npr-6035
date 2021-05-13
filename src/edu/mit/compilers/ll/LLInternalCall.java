package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLInternalCall implements LLInstruction {

  private final LLMethodDeclaration declaration;
  private final List<LLDeclaration> arguments;
  private final Optional<LLDeclaration> result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;
  private final Set<Web> aboveWebs;
  private final Set<Web> belowWebs;

  public LLInternalCall(LLMethodDeclaration declaration, List<LLDeclaration> arguments, Optional<LLDeclaration> result) {
    this.declaration = declaration;
    this.arguments = arguments;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
    this.aboveWebs = new HashSet<>();
    this.belowWebs = new HashSet<>();
  }

  public static class Builder {

    private final LLMethodDeclaration declaration;
    private final List<LLDeclaration> arguments;
    private final Optional<LLDeclaration> result;

    public Builder(LLMethodDeclaration declaration, Optional<LLDeclaration> result) {
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

  public Optional<LLDeclaration> getResult() {
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

  public void addAboveWeb(final Web web) {
    this.aboveWebs.add(web);
  }

  public void addBelowWeb(final Web web) {
    this.belowWebs.add(web);
  }

  public Set<Web> getAboveWebs() {
    return Set.copyOf(this.aboveWebs);
  }

  public Set<Web> getBelowWebs() {
    return Set.copyOf(this.belowWebs);
  }

  public Set<Web> getWebsAcross() {
    final Set<Web> intersection = new HashSet<>(this.aboveWebs);
    intersection.retainAll(this.belowWebs);
    return intersection;
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
    return result;
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLInternalCall((LLMethodDeclaration)uses.get(0), uses.subList(1, uses.size()), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public boolean defInRegister() {
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      return !webLocation.equals(Web.SPILL);
    } else {
      return false;
    }
  }

  @Override
  public boolean useInRegister(LLDeclaration use) {
    assert uses().contains(use) : "use must be expression";
    if (usesWebs.containsKey(use)) {
      final Web useWeb = usesWebs.get(use);
      final String webLocation = useWeb.getLocation();
      return !webLocation.equals(Web.SPILL);
    } else {
      return false;
    }
  }

  @Override
  public String getDefWebLocation() {
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return definition().get().location();
      } else {
        return webLocation;
      }
    } else {
      return definition().get().location();
    }
  }

  @Override
  public String getUseWebLocation(LLDeclaration use) {
    assert uses().contains(use) : "use not in uses";
    if (usesWebs.containsKey(use)) {
      final Web useWeb = usesWebs.get(use);
      final String webLocation = useWeb.getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return use.location();
      } else {
        return webLocation;
      }
    } else {
      return use.location();
    }
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    if (result.isPresent()) {
      s.append(result.get().prettyString(depth) + " = call " + declaration.prettyString(depth) + "(");
    } else {
      s.append("void call " + declaration.prettyString(depth) + "(");
    }

    if (arguments.size() > 0) {
      s.append(arguments.get(0).prettyString(depth));

      for (int i = 1; i < arguments.size(); i++) {
        s.append(", " + arguments.get(i).prettyString(depth));
      }
    }

    s.append(")");

    int alignment = 32 - depth * 2 - s.length();
    s.append(" ".repeat(alignment > 0 ? alignment : 1) + "; webs { ");
    if (definitionWeb.isPresent()) {
      s.append(result.get().prettyString(depth) + " => (" + definitionWeb.get().getIndex() + ", " + definitionWeb.get().getLocation() + "), ");
    }
    for (final Map.Entry<LLDeclaration, Web> entry : usesWebs.entrySet()) {
      s.append(entry.getKey().prettyString(depth) + " => (" + entry.getValue().getIndex() + ", " + entry.getValue().getLocation() + "), ");
    }
    s.append("}");

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
    if (result.isPresent()) {
      s.append(indent(depth + 1) + "result: " + result.get().debugString(depth + 1) + ",\n");
    }
    if (definitionWeb.isPresent()) {
      s.append(indent(depth + 1) + "definitionWeb: " + definitionWeb.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth + 1) + "usesWebs: {\n");
    for (final Map.Entry<LLDeclaration, Web> entry : usesWebs.entrySet()) {
      s.append(indent(depth + 2) + entry.getKey().debugString(depth + 2) + " => " + entry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLInternalCall that) {
  //   return declaration.equals(that.declaration)
  //     && arguments.equals(that.arguments)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLInternalCall && sameValue((LLInternalCall)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(declaration, arguments, result);
  // }

}
