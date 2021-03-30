package edu.mit.compilers.ll;

public class LLIntegerLiteral implements LLInstruction {

  private final long value;
  private final LLDeclaration result;

  public LLIntegerLiteral(long value, LLDeclaration result) {
    this.value = value;
    this.result = result;
  }

  public long getValue() {
    return this.value;
  }

  public LLDeclaration getResult() {
    return this.result;
  }

  @Override
  public String debugString(int depth) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }
}
