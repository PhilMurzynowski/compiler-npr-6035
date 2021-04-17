package edu.mit.compilers.ll;

public interface LLDeclaration extends LLNode {

  /**
   * @return the location of this declaration as found in the assembly code;
   *  this could be a register, offset into memory, label, etc.
   */
  String location();

  public String prettyStringDeclaration(int depth);
  public String toUniqueDeclarationString();

}
