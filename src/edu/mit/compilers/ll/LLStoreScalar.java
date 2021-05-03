package edu.mit.compilers.ll;

import java.util.*; 
import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLStoreScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration expression;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;

  public LLStoreScalar(LLScalarFieldDeclaration declaration, LLDeclaration expression) {
    this.declaration = declaration;
    this.expression = expression;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public LLDeclaration getExpression() {
    return this.expression;
  }

  public LLScalarFieldDeclaration getDeclaration() {
    return this.declaration;
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
    return List.of(expression);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(declaration);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLStoreScalar(declaration, uses.get(0));
  }
  
  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String getDefWebLocation() {
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return declaration.location();
      } else {
        return webLocation;
      }
    } else {
      return declaration.location();
    }
  }

  @Override
  public String getUseWebLocation(LLDeclaration use) {
    assert use == expression : "use must be expression";
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
    return "store " + declaration.prettyString(depth) + ", " + expression.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLStoreScalar {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
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

  // private boolean sameValue(LLStoreScalar that) {
  //   return declaration.equals(that.declaration)
  //     && expression.equals(that.expression);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLStoreScalar && sameValue((LLStoreScalar)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(declaration, expression);
  // }

}
