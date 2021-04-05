package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

//Note(phil): considering replacing with a ControlFlowGraph
public class LLBoundsCheck implements LLInstruction {

  private final LLArrayFieldDeclaration arrayDeclaration;
  private final LLDeclaration index;

  public LLBoundsCheck(LLArrayFieldDeclaration arrayDeclaration, LLDeclaration index) {
    this.arrayDeclaration = arrayDeclaration;
    this.index = index;
  }

  public LLArrayFieldDeclaration getArrayDeclaration() {
    return arrayDeclaration;
  }

  public LLDeclaration getIndex() {
    return index;
  }

  @Override
  public String prettyString(int depth) {
    return "bounds check " + arrayDeclaration.prettyString(depth) +
      " size: " + arrayDeclaration.getLength() + " index: " + index.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLBoundsCheck {\n");
    s.append(indent(depth + 1) + "arrayDeclaration: " + arrayDeclaration.prettyString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
