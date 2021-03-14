package edu.mit.compilers.ast;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTFieldDeclaration implements ASTNode {

  private final VariableType type;
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

    public String getIdentifier() {
      return identifier;
    }

    public Optional<ASTIntegerLiteral> getLength(){
      return length;
    }

    public String prettyString(int depth) {
      if (length.isPresent()) {
        return identifier + "[" + length.get().prettyString(depth) + "]";
      } else {
        return identifier;
      }
    }

    public String debugString(int depth) {
      StringBuilder s = new StringBuilder();
      s.append("Identifier {\n");
      s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
      if (length.isPresent()) {
        s.append(indent(depth + 1) + "length: " + length.get().debugString(depth + 1) + ",\n");
      }
      s.append(indent(depth) + "}");
      return s.toString();
    }

  }

  private ASTFieldDeclaration(VariableType type, List<Identifier> identifiers) {
    this.type = type;
    this.identifiers = identifiers;
  }

  public static class Builder {

    private VariableType type;
    private List<Identifier> identifiers;

    public Builder() {
      type = null;
      identifiers = new ArrayList<>();
    }

    public Builder withType(VariableType type) {
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

  public VariableType getType() {
    return type;
  }

  public List<Identifier> getIdentifiers() {
    return new ArrayList<>(identifiers);
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    if (type.equals(VariableType.INTEGER)) {
      s.append("int ");
    } else /* if (type.equals(VariableType.BOOLEAN)) */ {
      s.append("bool ");
    }
    s.append(identifiers.get(0).prettyString(depth));
    for (int i = 1; i < identifiers.size(); ++i) {
      s.append(", " + identifiers.get(i).prettyString(depth));
    }
    s.append(";");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTFieldDeclaration {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "identifiers: [\n");
    for (Identifier identifier : identifiers) {
      s.append(indent(depth + 2) + identifier.debugString(depth + 2) + ",\n");
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
