package edu.mit.compilers.hl;

public class HLStringLiteral implements HLArgument {

  private final HLStringLiteralDeclaration declaration;

  public HLStringLiteral(HLStringLiteralDeclaration declaration) {
    throw new RuntimeException("not implemented");
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
