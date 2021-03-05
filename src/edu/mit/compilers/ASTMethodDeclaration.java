package edu.mit.compilers;

import java.util.List;

class ASTMethodDeclaration implements ASTNode {

  public enum Type {
    INTEGER,
    BOOLEAN,
    VOID,
  }

  private final Type type;
  private final String identifier;
  private final List<String> arguments;
  private final ASTBlock block;

  public ASTMethodDeclaration(Type type, String identifier, List<String> arguments, ASTBlock block) {
    this.type = type;
    this.identifier = identifier;
    this.arguments = arguments;
    this.block = block;
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
