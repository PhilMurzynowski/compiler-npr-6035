package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLStoreArrayStatement implements HLStoreStatement {

  private final HLArrayFieldDeclaration declaration; 
  private final HLExpression index;
  private final HLExpression expression;

  public HLStoreArrayStatement(HLArrayFieldDeclaration declaration, HLExpression index, HLExpression expression) {
    this.declaration = declaration;
    this.index = index;
    this.expression = expression;
  }

  public HLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getIndex() {
    return index;
  }

  public HLExpression getExpression() {
    return expression;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLStoreArrayStatement {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
