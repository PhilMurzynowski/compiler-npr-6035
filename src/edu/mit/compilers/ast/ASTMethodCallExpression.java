package edu.mit.compilers.ast;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTMethodCallExpression implements ASTExpression {

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

  public String getIdentifier() {
    return identifier;
  }

  public List<ASTArgument> getArguments() {
    return new ArrayList<>(arguments);
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTArgument.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTExpression.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append(identifier);
    s.append("(");
    if (arguments.size() > 0) {
      s.append(arguments.get(0).prettyString(depth));
      for (int i = 1; i < arguments.size(); ++i) {
        s.append(", " + arguments.get(i).prettyString(depth));
      }
    }
    s.append(")");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTMethodCallExpression {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "arguments: [\n");
    for (ASTArgument argument : arguments) {
      s.append(indent(depth + 2) + argument.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
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
