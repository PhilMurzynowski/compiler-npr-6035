package edu.mit.compilers.ast;

public class ASTBreakStatement implements ASTStatement {

  public ASTBreakStatement() { }

  @Override
  public void accept(ASTNode.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void accept(ASTStatement.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    return "break;";
  }

  @Override
  public String debugString(int depth) {
    return "ASTBreakStatement { }";
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
