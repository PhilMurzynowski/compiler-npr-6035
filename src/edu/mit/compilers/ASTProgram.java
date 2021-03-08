package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;

class ASTProgram implements ASTNode {

  private final List<ASTImportDeclaration> importDeclarations;
  private final List<ASTFieldDeclaration> fieldDeclarations;
  private final List<ASTMethodDeclaration> methodDeclarations;

  private ASTProgram(List<ASTImportDeclaration> importDeclarations, List<ASTFieldDeclaration> fieldDeclarations, List<ASTMethodDeclaration> methodDeclarations) {
    this.importDeclarations = importDeclarations;
    this.fieldDeclarations = fieldDeclarations;
    this.methodDeclarations = methodDeclarations;
  }

  public static class Builder {

    private final List<ASTImportDeclaration> importDeclarations;
    private final List<ASTFieldDeclaration> fieldDeclarations;
    private final List<ASTMethodDeclaration> methodDeclarations;

    public Builder() {
      importDeclarations = new ArrayList<>();
      fieldDeclarations = new ArrayList<>();
      methodDeclarations = new ArrayList<>();
    }

    public Builder addImportDeclaration(ASTImportDeclaration importDeclaration) {
      importDeclarations.add(importDeclaration);
      return this;
    }

    public Builder addFieldDeclaration(ASTFieldDeclaration fieldDeclaration) {
      fieldDeclarations.add(fieldDeclaration);
      return this;
    }

    public Builder addMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
      methodDeclarations.add(methodDeclaration);
      return this;
    }

    public ASTProgram build() {
      return new ASTProgram(List.copyOf(importDeclarations), List.copyOf(fieldDeclarations), List.copyOf(methodDeclarations));
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
