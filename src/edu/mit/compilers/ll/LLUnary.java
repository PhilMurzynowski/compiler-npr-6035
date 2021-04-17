package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

import edu.mit.compilers.common.UnaryExpressionType;

import static edu.mit.compilers.common.Utilities.indent;

public class LLUnary implements LLInstruction {

  private final UnaryExpressionType type;
  private final LLDeclaration expression;
  private final LLDeclaration result;

  public LLUnary(UnaryExpressionType type, LLDeclaration expression, LLDeclaration result) {
    this.type = type;
    this.expression = expression;
    this.result = result;
  }

  public UnaryExpressionType getType() {
    return type;
  }

  public LLDeclaration getExpression() {
    return expression;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(expression);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLUnary(type, uses.get(0), result);
  }

  @Override
  public String getUniqueExpressionString() {
    StringBuilder exprBuilder =  new StringBuilder();

    if (type == UnaryExpressionType.NOT || type ==  UnaryExpressionType.NEGATE) {
      exprBuilder.append(type.toString());
      exprBuilder.append(expression.toUniqueDeclarationString());
    } else if (type == UnaryExpressionType.INCREMENT || type == UnaryExpressionType.INCREMENT) {
      exprBuilder.append(expression.toUniqueDeclarationString());
      exprBuilder.append(type.toString());
    } else {
      throw new RuntimeException("unreachable");
    }

    return exprBuilder.toString();
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = ");

    if (type.equals(UnaryExpressionType.NOT)) {
      s.append("not");
    } else if (type.equals(UnaryExpressionType.NEGATE)) {
      s.append("neg");
    } else if (type.equals(UnaryExpressionType.INCREMENT)) {
      s.append("inc");
    } else if (type.equals(UnaryExpressionType.DECREMENT)) {
      s.append("dec");
    } else {
      throw new RuntimeException("unreachable");
    }

    s.append(" " + expression.prettyString(depth));

    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLUnary {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("not implemented");
  }

}
