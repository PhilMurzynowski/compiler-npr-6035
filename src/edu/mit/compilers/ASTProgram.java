package edu.mit.compilers;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.Utilities.indent;

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

  @Override
  public void accept(ASTNode.Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    for (ASTImportDeclaration importDeclaration : importDeclarations) {
      s.append(indent(depth) + importDeclaration.prettyString(depth) + "\n");
    }
    for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
      s.append(indent(depth) + fieldDeclaration.prettyString(depth) + "\n");
    }
    for (ASTMethodDeclaration methodDeclaration : methodDeclarations) {
      s.append(indent(depth) + methodDeclaration.prettyString(depth) + "\n");
    }
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTProgram {\n");
    s.append(indent(depth + 1) + "importDeclarations: [\n");
    for (ASTImportDeclaration importDeclaration : importDeclarations) {
      s.append(indent(depth + 2) + importDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "fieldDeclarations: [\n");
    for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
      s.append(indent(depth + 2) + fieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "methodDeclarations: [\n");
    for (ASTMethodDeclaration methodDeclaration : methodDeclarations) {
      s.append(indent(depth + 2) + methodDeclaration.debugString(depth + 2) + ",\n");
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
