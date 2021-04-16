package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLCompare implements LLInstruction {

  private final LLDeclaration left;
  private final ComparisonType type;
  private final LLDeclaration right;

  public LLCompare(LLDeclaration left, ComparisonType type, LLDeclaration right) {
    this.left = left;
    this.type = type;
    this.right = right;
  }

  public LLDeclaration getLeft() {
    return left;
  }

  public ComparisonType getType() {
    return type;
  }

  public LLDeclaration getRight() {
    return right;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(left, right);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.empty();
  }

  @Override
  public String prettyString(int depth) {
    return "cmp " + type.prettyString(depth) + " " + left.prettyString(depth) + ", " + right.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLCompare {\n");
    s.append(indent(depth + 1) + "left: " + left.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "right: " + right.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
