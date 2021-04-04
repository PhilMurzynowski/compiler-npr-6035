package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadScalar implements LLInstruction {

  private final LLScalarFieldDeclaration declaration;
  private final LLDeclaration result;
  
  public LLLoadScalar(LLScalarFieldDeclaration declaration, LLDeclaration result) {
    this.declaration = declaration;
    this.result = result;
  }

  public LLScalarFieldDeclaration getDeclaration() {
    return this.declaration;
  }

  public LLDeclaration getResult() {
    return this.result;
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = load " + declaration.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLoadScalar {\n");
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
