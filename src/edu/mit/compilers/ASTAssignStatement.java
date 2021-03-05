package edu.mit.compilers;

import java.util.Optional;

class ASTAssignStatement implements ASTStatement {

  public enum Type {
    ASSIGN,
    ADD_ASSIGN,
    SUBTRACT_ASSIGN,
    INCREMENT,
    DECREMENT,
  }

  private final ASTLocationExpression location;
  private final Type type;
  private final Optional<ASTExpression> expression;

  public ASTAssignStatement(ASTLocationExpression location, Type type, Optional<ASTExpression> expression) {
    this.location = location;
    this.type = type;
    this.expression = expression;
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
