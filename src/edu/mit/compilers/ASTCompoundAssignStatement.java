package edu.mit.compilers;

import java.util.Optional;

class ASTCompoundAssignStatement implements ASTStatement {

  public enum Type {
    ADD,
    SUBTRACT,
    INCREMENT,
    DECREMENT,
  }

  private final ASTLocationExpression location;
  private final Type type;
  private final Optional<ASTExpression> expression;

  private ASTCompoundAssignStatement(ASTLocationExpression location, Type type, Optional<ASTExpression> expression) {
    this.location = location;
    this.type = type;
    this.expression = expression;
  }

  public static class Builder {

    private ASTLocationExpression location;
    private Type type;
    private Optional<ASTExpression> expression;

    public Builder() {
      location = null;
      type = null;
      expression = Optional.empty();
    }

    public Builder withLocation(ASTLocationExpression location) {
      this.location = location;
      return this;
    }

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder withExpression(ASTExpression expression) {
      this.expression = Optional.of(expression);
      return this;
    }

    public ASTCompoundAssignStatement build() {
      assert location != null;
      assert type != null;

      return new ASTCompoundAssignStatement(location, type, expression);
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
