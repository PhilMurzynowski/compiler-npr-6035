package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;

class ASTMethodCallExpression implements ASTExpression {

  private final String identifier;
  private final List<ASTArgument> arguments;

  private ASTMethodCallExpression(String identifier, List<ASTArgument> arguments) {
    this.identifier = identifier;
    this.arguments = arguments;
  }

  public static class Builder {

    private String identifier;
    private List<ASTArgument> arguments;

    public Builder() {
      identifier = null;
      arguments = new ArrayList<>();
    }

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder addArgument(ASTArgument argument) {
      arguments.add(argument);
      return this;
    }

    public ASTMethodCallExpression build() {
      assert identifier != null;

      return new ASTMethodCallExpression(identifier, List.copyOf(arguments));
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
