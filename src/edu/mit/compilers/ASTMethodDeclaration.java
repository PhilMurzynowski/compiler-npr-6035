package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;

class ASTMethodDeclaration implements ASTNode {

  public enum Type {
    INTEGER,
    BOOLEAN,
    VOID,
  }

  private final Type type;
  private final String identifier;
  private final List<Argument> arguments;
  private final ASTBlock block;

  public static class Argument {

    public enum Type {
      INTEGER,
      BOOLEAN,
    }

    private final Type type;
    private final String identifier;

    public Argument(Type type, String identifier) {
      this.type = type;
      this.identifier = identifier;
    }

    public static class Builder {

      private Type type;
      private String identifier;

      public Builder() { }

      public Builder withType(Type type) {
        this.type = type;
        return this;
      }

      public Builder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
      }

      public Argument build() {
        return new Argument(type, identifier);
      }

    }

  }

  private ASTMethodDeclaration(Type type, String identifier, List<Argument> arguments, ASTBlock block) {
    this.type = type;
    this.identifier = identifier;
    this.arguments = arguments;
    this.block = block;
  }

  public static class Builder {

    private Type type;
    private String identifier;
    private final List<Argument> arguments;
    private ASTBlock block;

    public Builder() {
      arguments = new ArrayList<>();
    }

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder addArgument(Argument argument) {
      arguments.add(argument);
      return this;
    }

    public Builder withBlock(ASTBlock block) {
      this.block = block;
      return this;
    }

    public ASTMethodDeclaration build() {
      return new ASTMethodDeclaration(type, identifier, List.copyOf(arguments), block);
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
