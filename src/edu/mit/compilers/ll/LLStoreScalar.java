package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

import static edu.mit.compilers.common.Utilities.indent;

public class LLStoreScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration expression;

  public LLStoreScalar(LLScalarFieldDeclaration declaration, LLDeclaration expression) {
    this.declaration = declaration;
    this.expression = expression;
  }

  public LLDeclaration getExpression() {
    return this.expression;
  }

  public LLScalarFieldDeclaration getDeclaration() {
    return this.declaration;
  }

  @Override
  public List<LLDeclaration> uses() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public Optional<LLDeclaration> definition() {
    throw new RuntimeException("not implemented");
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
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
