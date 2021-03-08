package edu.mit.compilers;

class ASTAssignStatement implements ASTStatement {

  private final ASTLocationExpression location;
  private final ASTExpression expression;

  private ASTAssignStatement(ASTLocationExpression location, ASTExpression expression) {
    this.location = location;
    this.expression = expression;
  }

  public static class Builder {

    private ASTLocationExpression location;
    private ASTExpression expression;

    public Builder() {
      location = null;
      expression = null;
    }

    public Builder withLocation(ASTLocationExpression location) {
      this.location = location;
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = expression;
      return this;
    }

    public ASTAssignStatement build() {
      assert location != null;
      assert expression != null;

      return new ASTAssignStatement(location, expression);
    }
  }

  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    throw new RuntimeException("not implemented");
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
