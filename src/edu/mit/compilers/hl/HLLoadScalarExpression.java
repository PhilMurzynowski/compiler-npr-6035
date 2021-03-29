package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLLoadScalarExpression implements HLLoadExpression {

  private final HLScalarFieldDeclaration declaration;

  public HLLoadScalarExpression(HLScalarFieldDeclaration declaration) {
    this.declaration = declaration;
  }

  public HLScalarFieldDeclaration getDeclaration() {
    return declaration;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLLoadScalarExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
