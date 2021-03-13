package edu.mit.compilers.ast;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTMethodDeclaration implements ASTNode {

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

      public Builder() {
        type = null;
        identifier = null;
      }

      public Builder withType(Type type) {
        this.type = type;
        return this;
      }

      public Builder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
      }

      public Argument build() {
        assert type != null;
        assert identifier != null;

        return new Argument(type, identifier);
      }

    }

    public String prettyString(int depth) {
      StringBuilder s = new StringBuilder();
      if (type.equals(Type.INTEGER)) {
        s.append("int ");
      } else /* if (type.equals(Type.BOOLEAN)) */ {
        s.append("bool ");
      }
      s.append(identifier);
      return s.toString();
    }

    public String debugString(int depth) {
      StringBuilder s = new StringBuilder();
      s.append("Argument {\n");
      s.append(indent(depth + 1) + "type: " + type + ",\n");
      s.append(indent(depth + 1) + "indentifier: " + identifier + ",\n");
      s.append(indent(depth) + "}");
      return s.toString();
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
      type = null;
      identifier = null;
      arguments = new ArrayList<>();
      block = null;
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
      assert type != null;
      assert identifier != null;
      assert block != null;

      return new ASTMethodDeclaration(type, identifier, List.copyOf(arguments), block);
    }

  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    if (type.equals(Type.INTEGER)) {
      s.append("int ");
    } else if (type.equals(Type.BOOLEAN)) {
      s.append("bool ");
    } else /* if (type.equals(Type.VOID)) */ {
      s.append("void ");
    }
    s.append(identifier + "(");
    if (arguments.size() > 0) {
      s.append(arguments.get(0).prettyString(depth));

      for (int i = 1; i < arguments.size(); ++i) {
        s.append(", " + arguments.get(i).prettyString(depth));
      }
    }
    s.append(") ");
    s.append(block.prettyString(depth));
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTMethodDeclaration {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "arguments: [\n");
    for (Argument argument : arguments) {
      s.append(indent(depth + 2) + argument.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "block: " + block.debugString(depth + 1) + ",\n");
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
