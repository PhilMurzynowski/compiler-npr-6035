package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLIntegerLiteral implements LLInstruction {

  private final long value;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;

  public LLIntegerLiteral(long value, LLDeclaration result) {
    this.value = value;
    this.result = result;
    this.definitionWeb = Optional.empty();
  }

  public long getValue() {
    return this.value;
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
    throw new RuntimeException("No uses");
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of();
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return this;
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
    throw new RuntimeException("no uses");
  }

  @Override
  public String getDefWebLocation() {
    LLDeclaration definition = definition().get();
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return definition.location();
      } else {
        return webLocation;
      }
    } else {
      return definition.location();
    }
  }

  @Override
  public String getUseWebLocation(LLDeclaration use) {
    throw new RuntimeException("No uses");
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = $" + value);

    s.append(" ".repeat(32 - depth * 2 - s.length()) + "; webs { ");
    if (definitionWeb.isPresent()) {
      s.append(result.prettyString(depth) + " => (" + definitionWeb.get().getIndex() + ", " + definitionWeb.get().getLocation() + "), ");
    }
    s.append("}");

    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLIntegerLiteral {\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    if (definitionWeb.isPresent()) {
      s.append(indent(depth + 1) + "definitionWeb: " + definitionWeb.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLIntegerLiteral that) {
  //   return value == that.value
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLIntegerLiteral && sameValue((LLIntegerLiteral)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(value, result);
  // }

}
