package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

import static edu.mit.compilers.common.Utilities.indent;

public class LLStoreArray implements LLInstruction {
  
  private final LLArrayFieldDeclaration declaration;
  private final LLDeclaration index;
  private final LLDeclaration expression;

  public LLStoreArray(LLArrayFieldDeclaration declaration, LLDeclaration index, LLDeclaration expression) {
    this.declaration = declaration;
    this.index = index;
    this.expression = expression;
  }

  public LLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public LLDeclaration getIndex() {
    return index;
  }

  public LLDeclaration getExpression() {
    return expression;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(expression, index);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(declaration);
  }
  
  @Override
  public String prettyString(int depth) {
    return "store " + declaration.prettyString(depth) + ", " + index.prettyString(depth) + ", " + expression.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLStoreArray {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
