package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

// DONE: Noah
public class HLStoreScalarStatement implements HLStoreStatement {

  private final HLScalarFieldDeclaration declaration;
  private final HLExpression expression;

  public HLStoreScalarStatement(HLScalarFieldDeclaration declaration, HLExpression expression) {
    this.declaration = declaration;
    this.expression = expression;
  }

  public HLScalarFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getExpression() {
    return expression;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append(indent(depth) + "HLStoreScalarStatement {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
