package edu.mit.compilers.ast;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTMethodCallStatement implements ASTStatement {

  private final ASTMethodCallExpression call;

  public ASTMethodCallStatement(ASTMethodCallExpression call) {
    this.call = call;
  }

  public ASTMethodCallExpression getCall() {
    return call;
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTStatement.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append(call.prettyString(depth));
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTMethodCallStatement {\n");
    s.append(indent(depth + 1) + "call: " + call.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  @Override
  public boolean equals(Object that) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int hashCode() {
    throw new RuntimeException("not implemented");
  }

}
