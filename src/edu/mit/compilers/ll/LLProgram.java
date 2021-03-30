package edu.mit.compilers.ll;

import java.util.List;
import java.util.ArrayList;

public class LLProgram implements LLNode {

  private final List<LLImportDeclaration> importDeclarations;
  private final List<LLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLStringLiteralDeclaration> stringLiteralDeclarations;
  private final List<LLMethodDeclaration> methodDeclarations;

  public LLProgram(
    final List<LLImportDeclaration> importDeclarations,
    final List<LLGlobalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<LLGlobalArrayFieldDeclaration> arrayFieldDeclarations,
    final List<LLStringLiteralDeclaration> stringLiteralDeclarations,
    final List<LLMethodDeclaration> methodDeclarations)
  {
    this.importDeclarations = importDeclarations;
    this.scalarFieldDeclarations = scalarFieldDeclarations;
    this.arrayFieldDeclarations = arrayFieldDeclarations;
    this.stringLiteralDeclarations = stringLiteralDeclarations;
    this.methodDeclarations = methodDeclarations;
  }

  public static class Builder {

    private final List<LLImportDeclaration> importDeclarations;
    private final List<LLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
    private final List<LLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
    private final List<LLStringLiteralDeclaration> stringLiteralDeclarations;
    private final List<LLMethodDeclaration> methodDeclarations;

    public Builder() {
      importDeclarations = new ArrayList<>();
      scalarFieldDeclarations = new ArrayList<>();
      arrayFieldDeclarations = new ArrayList<>();
      stringLiteralDeclarations = new ArrayList<>();
      methodDeclarations = new ArrayList<>();
    }

    public Builder addImport(LLImportDeclaration importDeclaration) {
      importDeclarations.add(importDeclaration);
      return this;
    }

    public Builder addScalar(LLGlobalScalarFieldDeclaration scalarDeclaration) {
      scalarFieldDeclarations.add(scalarDeclaration);
      return this;
    }

    public Builder addArray(LLGlobalArrayFieldDeclaration arrayDeclaration) {
      arrayFieldDeclarations.add(arrayDeclaration);
      return this;
    }

    public Builder addMethod(LLMethodDeclaration methodDeclaration) {
      methodDeclarations.add(methodDeclaration);
      return this;
    }

    public Builder addString(LLStringLiteralDeclaration string) {
      stringLiteralDeclarations.add(string);
      return this;
    }

    public LLProgram build() {
      return new LLProgram(List.copyOf(importDeclarations), List.copyOf(scalarFieldDeclarations), List.copyOf(arrayFieldDeclarations), List.copyOf(stringLiteralDeclarations), List.copyOf(methodDeclarations));
    }

  }

  public List<LLImportDeclaration> getImportDeclarations() {
    return this.importDeclarations;
  }

  public List<LLGlobalScalarFieldDeclaration> getScalarFieldDeclarations() {
    return this.scalarFieldDeclarations;
  }

  public List<LLGlobalArrayFieldDeclaration> getArrayFieldDeclarations() {
    return this.arrayFieldDeclarations;
  }

  public List<LLStringLiteralDeclaration> getStringLiteralDeclarations() {
    return this.stringLiteralDeclarations;
  }

  public List<LLMethodDeclaration> getMethodDeclarations() {
    return this.methodDeclarations;
  }

  @Override
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
