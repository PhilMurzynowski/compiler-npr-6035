package edu.mit.compilers;

import java.util.List;

class ASTFieldDeclaration implements ASTNode {

  public enum Type {
    INTEGER,
    BOOLEAN,
  }

  private final Type type;
  private final List<String> identifiers;

  public ASTFieldDeclaration(Type type, List<String> identifiers) {
    this.type = type;
    this.identifiers = identifiers;
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
