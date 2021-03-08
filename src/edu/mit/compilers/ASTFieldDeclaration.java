package edu.mit.compilers;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

class ASTFieldDeclaration implements ASTNode {

  public enum Type {
    INTEGER,
    BOOLEAN,
  }

  private final Type type;
  private final List<Identifier> identifiers;

  public static class Identifier {

    private final String identifier;
    private final Optional<ASTIntegerLiteral> length;

    private Identifier(String identifier, Optional<ASTIntegerLiteral> length) {
      this.identifier = identifier;
      this.length = length;
    }

    public static class Builder {

      private String identifier;
      private Optional<ASTIntegerLiteral> length;

      public Builder() {
        identifier = null;
        length = Optional.empty();
      }

      public Builder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
      }

      public Builder withLength(ASTIntegerLiteral length) {
        this.length = Optional.of(length);
        return this;
      }

      public Identifier build() {
        assert identifier != null;

        return new Identifier(identifier, length);
      }

    }

  }

  private ASTFieldDeclaration(Type type, List<Identifier> identifiers) {
    this.type = type;
    this.identifiers = identifiers;
  }

  public static class Builder {

    private Type type;
    private List<Identifier> identifiers;

    public Builder() {
      type = null;
      identifiers = new ArrayList<>();
    }

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder addIdentifier(Identifier identifier) {
      identifiers.add(identifier);
      return this;
    }

    public ASTFieldDeclaration build() {
      assert type != null;
      assert identifiers.size() > 0;

      return new ASTFieldDeclaration(type, List.copyOf(identifiers));
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
