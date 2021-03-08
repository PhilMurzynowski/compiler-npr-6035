package edu.mit.compilers;

import java.util.Optional;

class ASTLocationExpression implements ASTExpression {

  private final String identifier;
  private final Optional<ASTExpression> offset;

  private ASTLocationExpression(String identifier, Optional<ASTExpression> offset) {
    this.identifier = identifier;
    this.offset = offset;
  }

  public static class Builder {

    private String identifier;
    private Optional<ASTExpression> offset;

    public Builder() {
      identifier = null;
      offset = Optional.empty();
    }

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder withOffset(ASTExpression offset) {
      this.offset = Optional.of(offset);
      return this;
    }

    public ASTLocationExpression build() {
      assert identifier != null;

      return new ASTLocationExpression(identifier, offset);
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
