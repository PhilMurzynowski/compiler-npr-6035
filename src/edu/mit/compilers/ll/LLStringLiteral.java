package edu.mit.compilers.ll;

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
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
