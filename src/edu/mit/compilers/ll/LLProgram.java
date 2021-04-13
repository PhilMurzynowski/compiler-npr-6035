package edu.mit.compilers.ll;

import edu.mit.compilers.opt.Optimization;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

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

  public void accept(Optimization optimization) {
    List<LLDeclaration> globals = new ArrayList<>();
    globals.addAll(getScalarFieldDeclarations());
    globals.addAll(getArrayFieldDeclarations());
    globals = List.copyOf(globals);
    for (LLMethodDeclaration methodDeclaration : getMethodDeclarations()) {
      methodDeclaration.accept(optimization, globals);
    }
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    for (LLImportDeclaration importDeclaration : importDeclarations) {
      s.append(indent(depth) + importDeclaration.prettyStringDeclaration(depth) + "\n");
    }

    for (LLScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth) + scalarFieldDeclaration.prettyStringDeclaration(depth) + "\n");
    }

    for (LLArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth) + arrayFieldDeclaration.prettyStringDeclaration(depth) + "\n");
    }

    for (LLStringLiteralDeclaration stringLiteralDeclaration : stringLiteralDeclarations) {
      s.append(indent(depth) + stringLiteralDeclaration.prettyStringDeclaration(depth) + "\n");
    }

    for (LLMethodDeclaration methodDeclaration : methodDeclarations) {
      s.append(indent(depth) + methodDeclaration.prettyStringDeclaration(depth) + "\n");
    }

    return s.toString().strip();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLProgram {\n");
    s.append(indent(depth + 1) + "importDeclarations: [\n");
    for (LLImportDeclaration importDeclaration : importDeclarations) {
      s.append(indent(depth + 2) + importDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "scalarFieldDeclarations: [\n");
    for (LLScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth + 2) + scalarFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "arrayFieldDeclarations: [\n");
    for (LLArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth + 2) + arrayFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "stringLiteralDeclarations: [\n");
    for (LLStringLiteralDeclaration stringLiteralDeclaration : stringLiteralDeclarations) {
      s.append(indent(depth + 2) + stringLiteralDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "methodDeclarations: [\n");
    for (LLMethodDeclaration methodDeclaration : methodDeclarations) {
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
