package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLLoadArrayExpression implements HLLoadExpression {

  private final HLArrayFieldDeclaration declaration;
  private final HLExpression index;

  public HLLoadArrayExpression(HLArrayFieldDeclaration declaration, HLExpression index) {
    this.declaration = declaration;
    this.index = index;
  }

  public HLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getIndex() {
    return index;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append(indent(depth) + "HLLoadArrayExpression {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
