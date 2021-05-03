package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadArray implements LLInstruction {

  private final LLArrayFieldDeclaration location;
  private final LLDeclaration index;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;

  public LLLoadArray(LLArrayFieldDeclaration location, LLDeclaration index, LLDeclaration result) {
    this.location = location;
    this.index = index;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public LLArrayFieldDeclaration getLocation() {
    return location;
  }

  public LLDeclaration getIndex() {
    return index;
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
    return List.of(location, index);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLLoadArray((LLArrayFieldDeclaration)uses.get(0), uses.get(1), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = load " + location.prettyString(depth) + ", " + index.prettyString(depth);
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
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLoadArray {\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
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

  // private boolean sameValue(LLLoadArray that) {
  //   return location.equals(that.location)
  //     && index.equals(that.index)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLLoadArray && sameValue((LLLoadArray)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(location, index, result);
  // }

}
