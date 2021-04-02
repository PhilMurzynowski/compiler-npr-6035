package edu.mit.compilers.ll;

public class LLLength implements LLInstruction {

  private final LLArrayFieldDeclaration declaration;
  private final LLDeclaration result;

  public LLLength(LLArrayFieldDeclaration declaration, LLDeclaration result) {
    throw new RuntimeException("not implemented");
  }

  public LLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public LLDeclaration getResult() {
    return result;
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
