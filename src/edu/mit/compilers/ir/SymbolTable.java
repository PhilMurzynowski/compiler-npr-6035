package edu.mit.compilers.ir;
import java.util.*;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class SymbolTable {

  private final Optional<SymbolTable> parent;

  private final Set<String> importDeclarations;
  private final Map<String, MethodDeclaration> methodDeclarations;
  private final Map<String, ScalarDeclaration> scalarDeclarations;
  private final Map<String, ArrayDeclaration> arrayDeclarations;

  private static class MethodDeclaration {

    private final MethodType returnType;
    private final List<VariableType> argumentTypes;

    public MethodDeclaration(MethodType returnType, List<VariableType> argumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = new ArrayList<>(argumentTypes);
    }

    public MethodType getReturnType() {
      return returnType;
    }

    public String debugString(int depth) {
      StringBuilder s = new StringBuilder();
      s.append("MethodDeclaration {\n");
      s.append(indent(depth + 1) + "returnType: " + returnType + ",\n");
      s.append(indent(depth + 1) + "argumentTypes: [\n");
      for (VariableType argumentType : argumentTypes) {
        s.append(indent(depth + 2) + argumentType + ",\n");
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

  private static class ScalarDeclaration {

    private final VariableType type;

    public ScalarDeclaration(VariableType type) {
      this.type = type;
    }

    public VariableType getType() {
      return type;
    }

    public String debugString(int depth) {
      StringBuilder s = new StringBuilder();
      s.append("ScalarDeclaration {\n");
      s.append(indent(depth + 1) + "type: " + type + ",\n");
      s.append(indent(depth) + "}");
      return s.toString();
    }

    @Override
    public String toString() {
      return debugString(0);
    }

  }

  private static class ArrayDeclaration {

    private final VariableType type;

    public ArrayDeclaration(VariableType type) {
      this.type = type;
    }

    public VariableType getType() {
      return type;
    }

    public String debugString(int depth) {
      StringBuilder s = new StringBuilder();
      s.append("ArrayDeclaration {\n");
      s.append(indent(depth + 1) + "type: " + type + ",\n");
      s.append(indent(depth) + "}");
      return s.toString();
    }

    @Override
    public String toString() {
      return debugString(0);
    }

  }

  public SymbolTable() {
    this.parent = Optional.empty();
    this.importDeclarations = new HashSet<String>();
    this.methodDeclarations = new HashMap<String, MethodDeclaration>();
    this.scalarDeclarations = new HashMap<String, ScalarDeclaration>();
    this.arrayDeclarations  = new HashMap<String, ArrayDeclaration>();
  }

  public SymbolTable(SymbolTable parent) {
    this.parent = Optional.of(parent);
    this.importDeclarations = new HashSet<String>();
    this.methodDeclarations = new HashMap<String, MethodDeclaration>(); 
    this.scalarDeclarations = new HashMap<String, ScalarDeclaration>(); 
    this.arrayDeclarations  = new HashMap<String, ArrayDeclaration>(); 
  }

  public boolean exists(String identifier) {
    return importDeclarations.contains(identifier)
      || methodDeclarations.containsKey(identifier) 
      || scalarDeclarations.containsKey(identifier)
      || arrayDeclarations.containsKey(identifier);
  }

  public boolean importExists(String identifier) {
    return importDeclarations.contains(identifier)
      || (parent.isPresent() && parent.get().importExists(identifier));
  }

  public boolean methodExists(String identifier) {
    return methodDeclarations.containsKey(identifier)
      || (parent.isPresent() && parent.get().methodExists(identifier));   
  }

  public boolean scalarExists(String identifier) {
    return scalarDeclarations.containsKey(identifier) 
      || (parent.isPresent() && parent.get().scalarExists(identifier));
  }

  public boolean arrayExists(String identifier) {
    return arrayDeclarations.containsKey(identifier)
      || (parent.isPresent() && parent.get().arrayExists(identifier));
  }

  public void addImport(String identifier) {
    importDeclarations.add(identifier);
  }

  public void addMethod(String identifier, MethodType returnType, List<VariableType> argumentTypes) {
    methodDeclarations.put(identifier, new MethodDeclaration(returnType, argumentTypes));
  }

  public void addScalar(String identifier, VariableType type) {
    scalarDeclarations.put(identifier, new ScalarDeclaration(type));
  }

  public void addArray(String identifier, VariableType type) {
    arrayDeclarations.put(identifier, new ArrayDeclaration(type));
  }

  public MethodType methodReturnType(String identifier) {
    if (methodDeclarations.containsKey(identifier)) {
      return methodDeclarations.get(identifier).getReturnType();
    } else if (parent.isPresent()) {
      return parent.get().methodReturnType(identifier);
    } else {
      throw new RuntimeException("method not in symbol table");
    }
  }

  public List<VariableType> methodArgumentTypes(String identifier) {
    if (methodDeclarations.containsKey(identifier)) {
      return List.copyOf(methodDeclarations.get(identifier).argumentTypes);
    } else if (parent.isPresent()) {
      return parent.get().methodArgumentTypes(identifier);
    } else {
      throw new RuntimeException("method not in symbol table");
    }
  }

  public VariableType scalarType(String identifier) {
    if (scalarDeclarations.containsKey(identifier)) {
      return scalarDeclarations.get(identifier).getType();
    } else if (parent.isPresent()) {
      return parent.get().scalarType(identifier);
    } else {
      throw new RuntimeException("scalar not in symbol table");
    }
  }

  public VariableType arrayType(String identifier) {
    if (arrayDeclarations.containsKey(identifier)) {
      return arrayDeclarations.get(identifier).getType();
    } else if (parent.isPresent()) {
      return parent.get().arrayType(identifier);
    } else {
      throw new RuntimeException("array not in symbol table");
    }
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("SymbolTable {\n");
    if (parent.isPresent()) {
      s.append(indent(depth + 1) + "parent: " + parent.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth + 1) + "importDeclarations: {\n");
    for (String importDeclaration : importDeclarations) {
      s.append(indent(depth + 2) + importDeclaration + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "methodDeclarations: {\n");
    for (Map.Entry<String, MethodDeclaration> methodDeclarationEntry : methodDeclarations.entrySet()) {
      s.append(indent(depth + 2) + methodDeclarationEntry.getKey() + " => " + methodDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "scalarDeclarations: {\n");
    for (Map.Entry<String, ScalarDeclaration> scalarDeclarationEntry : scalarDeclarations.entrySet()) {
      s.append(indent(depth + 2) + scalarDeclarationEntry.getKey() + " => " + scalarDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth + 1) + "arrayDeclarations: {\n");
    for (Map.Entry<String, ArrayDeclaration> arrayDeclarationEntry : arrayDeclarations.entrySet()) {
      s.append(indent(depth + 2) + arrayDeclarationEntry.getKey() + " => " + arrayDeclarationEntry.getValue().debugString(depth + 2) + ",\n");
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
