package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration result;
  
  public LLLoadScalar(LLScalarFieldDeclaration declaration, LLDeclaration result) {
    this.declaration = declaration;
    this.result = result;
  }

  public LLScalarFieldDeclaration getDeclaration() {
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
    if (uses.get(0) instanceof LLConstantDeclaration constantDeclaration) {
      return new LLIntegerLiteral(constantDeclaration.getValue(), result);
    } else if (uses.get(0) instanceof LLScalarFieldDeclaration scalarFieldDeclaration) {
      return new LLLoadScalar(scalarFieldDeclaration, result);
    } else if (uses.get(0) instanceof LLAliasDeclaration aliasDeclaration) {
      return new LLCopy(aliasDeclaration, result);
    } else {
      System.err.println(uses.get(0));
      throw new RuntimeException("not implemented");
    }
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = load " + declaration.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLoadScalar {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  private boolean sameValue(LLLoadScalar that) {
    return declaration.equals(that.declaration)
      && result.equals(that.result);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLLoadScalar && sameValue((LLLoadScalar)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(declaration, result);
  }

}
