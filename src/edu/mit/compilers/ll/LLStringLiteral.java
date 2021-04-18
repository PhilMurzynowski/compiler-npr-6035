package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
// import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLStringLiteral implements LLInstruction {

  private final LLStringLiteralDeclaration declaration;
  private final LLDeclaration result;

  public LLStringLiteral(LLStringLiteralDeclaration declaration, LLDeclaration result) {
    this.declaration = declaration;
    this.result = result; 
  }

  public LLStringLiteralDeclaration getDeclaration() {
    return this.declaration;
  }

  public LLDeclaration getResult() {
    return this.result;
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
    return new LLStringLiteral((LLStringLiteralDeclaration)uses.get(0), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = " + declaration.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLStringLiteral {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLStringLiteral that) {
  //   return declaration.equals(that.declaration)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLStringLiteral && sameValue((LLStringLiteral)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(declaration, result);
  // }

}
