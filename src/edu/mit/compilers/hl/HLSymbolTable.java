package edu.mit.compilers.hl;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import static edu.mit.compilers.common.Utilities.indent;

public class HLSymbolTable {

  private final Optional<HLSymbolTable> parent;

  private final Map<String, HLImportDeclaration> importDeclarations;
  private final Map<String, HLStringLiteralDeclaration> stringLiteralDeclarations;
  private final Map<String, HLScalarFieldDeclaration> scalarFieldDeclarations;
  private final Map<String, HLArrayFieldDeclaration> arrayFieldDeclarations;
  private final Map<String, HLMethodDeclaration> methodDeclarations;

  public HLSymbolTable() {
    parent = Optional.empty();
    importDeclarations = new HashMap<>();
    stringLiteralDeclarations = new HashMap<>();
    scalarFieldDeclarations = new HashMap<>();
    arrayFieldDeclarations = new HashMap<>();
    methodDeclarations = new HashMap<>();
  }

  public HLSymbolTable(HLSymbolTable parent) {
    this.parent = Optional.of(parent);
    importDeclarations = new HashMap<>();
    stringLiteralDeclarations = new HashMap<>();
    scalarFieldDeclarations = new HashMap<>();
    arrayFieldDeclarations = new HashMap<>();
    methodDeclarations = new HashMap<>();
  }

  public void addImport(String identifier, HLImportDeclaration declaration) {
    importDeclarations.put(identifier, declaration);
  }

  public void addStringLiteral(String value, HLStringLiteralDeclaration declaration) {
    stringLiteralDeclarations.put(value, declaration);
  }

  public void addScalar(String identifier, HLScalarFieldDeclaration declaration) {
    scalarFieldDeclarations.put(identifier, declaration);
  }

  public void addArray(String identifier, HLArrayFieldDeclaration declaration) {
    arrayFieldDeclarations.put(identifier, declaration);
  }

  public void addMethod(String identifier, HLMethodDeclaration declaration) {
    methodDeclarations.put(identifier, declaration);
  }

  public HLImportDeclaration getImport(String identifier) {
    if (importDeclarations.containsKey(identifier)) {
      return importDeclarations.get(identifier);
    } else if (parent.isPresent()) {
      return parent.get().getImport(identifier);
    } else {
      throw new RuntimeException("import does not exist in the symbol table");
    }
  }

  public HLStringLiteralDeclaration getStringLiteral(String value) {
    if (stringLiteralDeclarations.containsKey(value)) {
      return stringLiteralDeclarations.get(value);
    } else if (parent.isPresent()) {
      return parent.get().getStringLiteral(value);
    } else {
      throw new RuntimeException("string literal does not exist in the symbol table");
    }
  }

  public List<HLStringLiteralDeclaration> getStringLiterals() {
    return List.copyOf(stringLiteralDeclarations.values());
  }

  public HLScalarFieldDeclaration getScalar(String identifier) {
    if (scalarFieldDeclarations.containsKey(identifier)) {
      return scalarFieldDeclarations.get(identifier);
    } else if (parent.isPresent()) {
      return parent.get().getScalar(identifier);
    } else {
      throw new RuntimeException("scalar does not exist in the symbol table");
    }
  }

  public HLArrayFieldDeclaration getArray(String identifier) {
    if (arrayFieldDeclarations.containsKey(identifier)) {
      return arrayFieldDeclarations.get(identifier);
    } else if (parent.isPresent()) {
      return parent.get().getArray(identifier);
    } else {
      throw new RuntimeException("array does not exist in the symbol table");
    }
  }

  public HLMethodDeclaration getMethod(String identifier) {
    if (methodDeclarations.containsKey(identifier)) {
      return methodDeclarations.get(identifier);
    } else if (parent.isPresent()) {
      return parent.get().getMethod(identifier);
    } else {
      throw new RuntimeException("method does not exist in the symbol table");
    }
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLSymbolTable {\n");
    if (parent.isPresent()) {
      s.append(indent(depth + 1) + "parent: " + parent.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth + 1) + "importDeclarations: {\n");
    for (Map.Entry<String, HLImportDeclaration> importDeclarationEntry : importDeclarations.entrySet()) {
      s.append(indent(depth + 2) + importDeclarationEntry.getKey() + " => " + importDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "stringLiteralDeclarations: {\n");
    for (Map.Entry<String, HLStringLiteralDeclaration> stringLiteralDeclarationEntry : stringLiteralDeclarations.entrySet()) {
      s.append(indent(depth + 2) + stringLiteralDeclarationEntry.getKey() + " => " + stringLiteralDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "scalarFieldDeclarations: {\n");
    for (Map.Entry<String, HLScalarFieldDeclaration> scalarDeclarationEntry : scalarFieldDeclarations.entrySet()) {
      s.append(indent(depth + 2) + scalarDeclarationEntry.getKey() + " => " + scalarDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "arrayFieldDeclarations: {\n");
    for (Map.Entry<String, HLArrayFieldDeclaration> arrayDeclarationEntry : arrayFieldDeclarations.entrySet()) {
      s.append(indent(depth + 2) + arrayDeclarationEntry.getKey() + " => " + arrayDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "methodDeclarations: {\n");
    for (Map.Entry<String, HLMethodDeclaration> methodDeclarationEntry : methodDeclarations.entrySet()) {
      s.append(indent(depth + 2) + methodDeclarationEntry.getKey() + " => " + methodDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
