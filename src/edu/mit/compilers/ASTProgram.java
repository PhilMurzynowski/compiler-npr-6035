package edu.mit.compilers;

import java.util.List;

class ASTProgram implements ASTNode {

  private final List<ASTImportDeclaration> importDeclarations;
  private final List<ASTFieldDeclaration> fieldDeclarations;
  private final List<ASTMethodDeclaration> methodDeclarations;

  public ASTProgram(List<ASTImportDeclaration> importDeclarations, List<ASTFieldDeclaration> fieldDeclarations, List<ASTMethodDeclaration> methodDeclarations) {
    this.importDeclarations = importDeclarations;
    this.fieldDeclarations = fieldDeclarations;
    this.methodDeclarations = methodDeclarations;
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
