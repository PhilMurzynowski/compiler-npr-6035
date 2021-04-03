package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLStringLiteral implements LLInstruction {

  private final LLStringLiteralDeclaration declaration;
  private final LLDeclaration result;

  public LLStringLiteral(LLStringLiteralDeclaration declaration, LLDeclaration result) {
    this.declaration = declaration;
    this.result = result; 
  }

  LLStringLiteralDeclaration getDeclaration() {
    return this.declaration;
  }

  LLDeclaration getResult() {
    return this.result;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLStringLiteral {\n");
    s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
