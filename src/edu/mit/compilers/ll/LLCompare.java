package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.common.*;
import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLCompare implements LLInstruction {

  private final LLDeclaration left;
  private final ComparisonType type;
  private final LLDeclaration right;
  private Map<LLDeclaration, Web> usesWebs;

  public LLCompare(LLDeclaration left, ComparisonType type, LLDeclaration right) {
    this.left = left;
    this.type = type;
    this.right = right;
    this.usesWebs = new HashMap<>();
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
  public void setDefinitionWeb(final Web web) {
    throw new RuntimeException("LLCompare does not define anything");
  }

  @Override
  public void addUsesWeb(final LLDeclaration definition, final Web web) {
    usesWebs.put(definition, web);
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
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLCompare(uses.get(0), type, uses.get(1));
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
    /*
    StringBuilder exprBuilder =  new StringBuilder();

    exprBuilder.append(this.getLeft().toUniqueDeclarationString());
    exprBuilder.append(" " + this.getType().toBinaryExpressionType().toString() + " ");
    exprBuilder.append(this.getRight().toUniqueDeclarationString());

    return exprBuilder.toString();
    */
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
    s.append(indent(depth + 1) + "usesWebs: {\n");
    for (final Map.Entry<LLDeclaration, Web> entry : usesWebs.entrySet()) {
      s.append(indent(depth + 2) + entry.getKey().debugString(depth + 2) + " => " + entry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLCompare that) {
  //   return left.equals(that.left)
  //     && type.equals(that.type)
  //     && right.equals(that.right);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLCompare && sameValue((LLCompare)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(left, type, right);
  // }

}
