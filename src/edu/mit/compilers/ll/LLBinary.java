package edu.mit.compilers.ll;

import edu.mit.compilers.common.*;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

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
    return left;
  }

  public BinaryExpressionType getType() {
    return type;
  }

  public LLDeclaration getRight() {
    return right;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(left, right);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLBinary(uses.get(0), type, uses.get(1), result);
  }

  @Override
  public String getUniqueExpressionString() {
    StringBuilder exprBuilder =  new StringBuilder();

    exprBuilder.append(this.getLeft().toUniqueDeclarationString());
    exprBuilder.append(" " + this.getType().toString() + " ");
    exprBuilder.append(this.getRight().toUniqueDeclarationString());

    return exprBuilder.toString();
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = ");

    if (type.equals(BinaryExpressionType.OR)) {
      s.append("or");
    } else if (type.equals(BinaryExpressionType.AND)) {
      s.append("and");
    } else if (type.equals(BinaryExpressionType.EQUAL)) {
      s.append("eq");
    } else if (type.equals(BinaryExpressionType.NOT_EQUAL)) {
      s.append("ne");
    } else if (type.equals(BinaryExpressionType.LESS_THAN)) {
      s.append("lt");
    } else if (type.equals(BinaryExpressionType.LESS_THAN_OR_EQUAL)) {
      s.append("le");
    } else if (type.equals(BinaryExpressionType.GREATER_THAN)) {
      s.append("gt");
    } else if (type.equals(BinaryExpressionType.GREATER_THAN_OR_EQUAL)) {
      s.append("ge");
    } else if (type.equals(BinaryExpressionType.ADD)) {
      s.append("add");
    } else if (type.equals(BinaryExpressionType.SUBTRACT)) {
      s.append("sub");
    } else if (type.equals(BinaryExpressionType.MULTIPLY)) {
      s.append("mul");
    } else if (type.equals(BinaryExpressionType.DIVIDE)) {
      s.append("div");
    } else if (type.equals(BinaryExpressionType.MODULUS)) {
      s.append("mod");
    } else {
      throw new RuntimeException("unreachable");
    }

    s.append(" " + left.prettyString(depth) + ", " + right.prettyString(depth));

    return s.toString();
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

  private boolean sameValue(LLBinary that) {
    return left.equals(that.left)
      && type.equals(that.type)
      && right.equals(that.right)
      && result.equals(that.result);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLBinary && sameValue((LLBinary)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, type, right, result);
  }

}
