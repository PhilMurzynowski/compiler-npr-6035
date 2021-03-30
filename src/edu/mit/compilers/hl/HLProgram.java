package edu.mit.compilers.hl;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class HLProgram implements HLNode {

  private final List<HLImportDeclaration> importDeclarations;
  private final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStringLiteralDeclaration> stringLiteralDeclarations;
  private final List<HLMethodDeclaration> methodDeclarations;

  public HLProgram(
    final List<HLImportDeclaration> importDeclarations,
    final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations,
    final List<HLStringLiteralDeclaration> stringLiteralDeclarations,
    final List<HLMethodDeclaration> methodDeclarations)
  {
    this.importDeclarations = importDeclarations;
    this.scalarFieldDeclarations = scalarFieldDeclarations;
    this.arrayFieldDeclarations = arrayFieldDeclarations;
    this.stringLiteralDeclarations = stringLiteralDeclarations;
    this.methodDeclarations = methodDeclarations;
  }

  public static class Builder {

    private final List<HLImportDeclaration> importDeclarations;
    private final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
    private final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
    private final List<HLStringLiteralDeclaration> stringLiteralDeclarations;
    private final List<HLMethodDeclaration> methodDeclarations;

    public Builder() {
      importDeclarations = new ArrayList<>();
      scalarFieldDeclarations = new ArrayList<>();
      arrayFieldDeclarations = new ArrayList<>();
      stringLiteralDeclarations = new ArrayList<>();
      methodDeclarations = new ArrayList<>();
    }

    public Builder addImport(HLImportDeclaration importDeclaration) {
      importDeclarations.add(importDeclaration);
      return this;
    }

    public Builder addScalar(HLGlobalScalarFieldDeclaration scalarDeclaration) {
      scalarFieldDeclarations.add(scalarDeclaration);
      return this;
    }

    public Builder addArray(HLGlobalArrayFieldDeclaration arrayDeclaration) {
      arrayFieldDeclarations.add(arrayDeclaration);
      return this;
    }

    public Builder addMethod(HLMethodDeclaration methodDeclaration) {
      methodDeclarations.add(methodDeclaration);
      return this;
    }

    public Builder addStrings(List<HLStringLiteralDeclaration> strings) {
      stringLiteralDeclarations.addAll(strings);
      return this;
    }

    public HLProgram build() {
      return new HLProgram(List.copyOf(importDeclarations), List.copyOf(scalarFieldDeclarations), List.copyOf(arrayFieldDeclarations), List.copyOf(stringLiteralDeclarations), List.copyOf(methodDeclarations));
    }

  }

  public List<HLImportDeclaration> getImportDeclarations() {
    return this.importDeclarations;
  }
  public List<HLGlobalScalarFieldDeclaration> getGlobalScalarFieldDeclarations() {
    return this.scalarFieldDeclarations;
  }
  public List<HLGlobalArrayFieldDeclaration> getGlobalArrayFieldDeclarations() {
    return this.arrayFieldDeclarations;
  }
  public List<HLStringLiteralDeclaration> getStringLiteralDeclarations() {
    return this.stringLiteralDeclarations;
  }
  public List<HLMethodDeclaration> getMethodDeclarations() {
    return this.methodDeclarations;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLProgram {\n");
    s.append(indent(depth + 1) + "importDeclarations: [\n");
    for (HLImportDeclaration importDeclaration : importDeclarations) {
      s.append(indent(depth + 2) + importDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "scalarFieldDeclarations: [\n");
    for (HLScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth + 2) + scalarFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "arrayFieldDeclarations: [\n");
    for (HLArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth + 2) + arrayFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "stringLiteralDeclarations: [\n");
    for (HLStringLiteralDeclaration stringLiteralDeclaration : stringLiteralDeclarations) {
      s.append(indent(depth + 2) + stringLiteralDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "methodDeclarations: [\n");
    for (HLMethodDeclaration methodDeclaration : methodDeclarations) {
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

}
