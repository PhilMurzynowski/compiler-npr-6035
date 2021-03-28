package edu.mit.compilers.ll;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLBinary implements LLInstruction {

  private final LLDeclaration left;
  private final BinaryExpressionType type;
  private final LLDeclaration right;
  private final LLDeclaration result;

  public LLBinary(LLDeclaration left, BinaryExpressionType type, LLDeclaration right, LLDeclaration result) {
    this.left = left;
    this.type = type;
    this.right = right;
    this.result = result;
  }

  public LLDeclaration getLeft() {
    throw new RuntimeException("not implemented");
  }

  public BinaryExpressionType getType() {
    throw new RuntimeException("not implemented");
  }

  public LLDeclaration getRight() {
    throw new RuntimeException("not implemented");
  }

  public LLDeclaration getResult() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLBinary {\n");
    s.append(indent(depth + 1) + "left: " + left.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "right: " + right.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
