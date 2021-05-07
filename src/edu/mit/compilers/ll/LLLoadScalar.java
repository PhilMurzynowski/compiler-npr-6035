package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;
  
  public LLLoadScalar(LLScalarFieldDeclaration declaration, LLDeclaration result) {
    this.declaration = declaration;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public LLScalarFieldDeclaration getDeclaration() {
    return this.declaration;
  }

  public LLDeclaration getResult() {
    return this.result;
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
    return List.of(declaration);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    if (uses.get(0) instanceof LLConstantDeclaration constantDeclaration) {
      return new LLIntegerLiteral(constantDeclaration.getValue(), result);
    } else if (uses.get(0) instanceof LLScalarFieldDeclaration scalarFieldDeclaration) {
      return new LLLoadScalar(scalarFieldDeclaration, result);
    } else if (uses.get(0) instanceof LLAliasDeclaration aliasDeclaration) {
      return new LLCopy(aliasDeclaration, result);
    } else {
      throw new RuntimeException("not implemented");
    }
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
    s.append(result.prettyString(depth) + " = load " + declaration.prettyString(depth));

    s.append(" ".repeat(32 - depth * 2 - s.length()) + "; webs { ");
    if (definitionWeb.isPresent()) {
      s.append(result.prettyString(depth) + " => (" + definitionWeb.get().getIndex() + ", " + definitionWeb.get().getLocation() + "), ");
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
    s.append("LLLoadScalar {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
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

  // private boolean sameValue(LLLoadScalar that) {
  //   return declaration.equals(that.declaration)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLLoadScalar && sameValue((LLLoadScalar)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(declaration, result);
  // }

}
