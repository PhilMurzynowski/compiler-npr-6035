package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;

class ASTBlock implements ASTNode {

  private final List<ASTFieldDeclaration> fieldDeclarations;
  private final List<ASTStatement> statements;

  private ASTBlock(List<ASTFieldDeclaration> fieldDeclarations, List<ASTStatement> statements) {
    this.fieldDeclarations = fieldDeclarations;
    this.statements = statements;
  }

  public static class Builder {

    private final List<ASTFieldDeclaration> fieldDeclarations;
    private final List<ASTStatement> statements;
    
    public Builder() {
      fieldDeclarations = new ArrayList<>();
      statements = new ArrayList<>();
    }

    public Builder addFieldDeclaration(ASTFieldDeclaration fieldDeclaration) {
      fieldDeclarations.add(fieldDeclaration);
      return this;
    }

    public Builder addStatement(ASTStatement statement) {
      statements.add(statement);
      return this;
    }

    public ASTBlock build() {
      return new ASTBlock(List.copyOf(fieldDeclarations), List.copyOf(statements));
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
