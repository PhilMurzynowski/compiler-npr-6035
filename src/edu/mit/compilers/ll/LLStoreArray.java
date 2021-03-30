package edu.mit.compilers.ll;

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
