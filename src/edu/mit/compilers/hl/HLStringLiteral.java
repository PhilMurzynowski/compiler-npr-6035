package edu.mit.compilers.hl;

import static edu.mit.compilers.common.Utilities.indent;

public class HLStringLiteral implements HLArgument {

  private final HLStringLiteralDeclaration declaration;

  public HLStringLiteral(HLStringLiteralDeclaration declaration) {
    this.declaration = declaration;
  }

  public HLStringLiteralDeclaration getDeclaration() {
    return this.declaration;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLStringLiteral {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
