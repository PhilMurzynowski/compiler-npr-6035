package edu.mit.compilers.ll;

public class LLIntegerLiteral implements LLInstruction {

  private final long value;
  private final LLDeclaration result;

  public LLIntegerLiteral(long value, LLDeclaration result) {
    throw new UnsupportedOperationException("not implemented");
  }

  public long getValue() {
    throw new UnsupportedOperationException("not implemented");
  }

  public LLDeclaration getResult() {
    throw new UnsupportedOperationException("not implemented");
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
