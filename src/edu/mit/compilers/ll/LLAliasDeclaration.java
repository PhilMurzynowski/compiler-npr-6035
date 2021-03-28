package edu.mit.compilers.ll;

public class LLAliasDeclaration implements LLDeclaration {

  private final int index;

  public LLAliasDeclaration(int index) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String location() {
    // TODO(rbd): Not quuuiiite right. Need to account for offsets of all other types of declarations.
    // return "-" + (index * 8) + "(%rbp)";
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
