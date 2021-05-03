package edu.mit.compilers.ll;

import java.util.*; 
import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLCopy implements LLInstruction {

  private final LLDeclaration input;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;

  public LLCopy(LLDeclaration input, LLDeclaration result) {
    this.input = input;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public LLDeclaration getInput() {
    return input;
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
    assert uses().contains(use) : "use must be expression";
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

  // private boolean sameValue(LLCopy that) {
  //   return input.equals(that.input)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLCopy && sameValue((LLCopy)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(input, result);
  // }

}
