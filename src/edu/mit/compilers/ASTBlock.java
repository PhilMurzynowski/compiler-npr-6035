package edu.mit.compilers;

import java.util.List;

class ASTBlock implements ASTNode {

  private final List<ASTFieldDeclaration> fieldDeclarations;
  private final List<ASTStatement> statements;

  public ASTBlock(List<ASTFieldDeclaration> fieldDeclarations, List<ASTStatement> statements) {
    this.fieldDeclarations = fieldDeclarations;
    this.statements = statements;
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
