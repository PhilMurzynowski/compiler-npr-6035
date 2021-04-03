package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLLengthExpression implements HLExpression {

  private final HLArrayFieldDeclaration declaration;

  public HLLengthExpression(HLArrayFieldDeclaration declaration) {
    this.declaration = declaration;
  }

  public HLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLLengthExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
