package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLException implements LLInstruction {

  public enum Type {
    OutOfBounds,
    NoReturnValue,
    DivideByZero,
  }

  private final Type type;

  public LLException(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String getDefWebLocation() {
    throw new RuntimeException("LLException does not define anything");
  }

  @Override
  public String getUseWebLocation(LLDeclaration use) {
    throw new RuntimeException("LLException does not use anything");
  }

  @Override
  public void setDefinitionWeb(final Web web) {
    throw new RuntimeException("LLException does not define anything");
  }

  @Override
  public void addUsesWeb(final LLDeclaration definition, final Web web) {
    throw new RuntimeException("LLException does not use anything");
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of();
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.empty();
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
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("except " + type);
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLException {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLException that) {
  //   return type.equals(that.type);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLException && sameValue((LLException)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(type);
  // }

}
