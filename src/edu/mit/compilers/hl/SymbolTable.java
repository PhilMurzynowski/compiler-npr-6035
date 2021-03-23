package edu.mit.compilers.hl;
import java.util.*;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class SymbolTable {

  // the symbol table of the enclosing scope, if one exists
  private final Optional<SymbolTable> parent;

  // the table contents: imports, methods, and fields.
  private final Set<String> importDeclarations;
  private final Map<String, MethodDeclaration> methodDeclarations;
  private final Map<String, ScalarDeclaration> scalarDeclarations;
  private final Map<String, ArrayDeclaration> arrayDeclarations;

  /**
   * Immutable type representing a method declaration, including its return type
   * and argument types.
   */
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

  /**
   * Immutable type representing a scalar field declaration, including its type.
   */
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

  /**
   * Immutable class representing an array field declaration, including its
   * type.
   */
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

  /**
   * Create an un-parented symbol table.
   */
  public SymbolTable() {
    this.parent = Optional.empty();
    this.importDeclarations = new HashSet<String>();
    this.methodDeclarations = new HashMap<String, MethodDeclaration>();
    this.scalarDeclarations = new HashMap<String, ScalarDeclaration>();
    this.arrayDeclarations  = new HashMap<String, ArrayDeclaration>();
  }

  /**
   * Create a symbol table with a parent table.
   *
   * @param parent the symbol table from this table's enclosing scope.
   */
  public SymbolTable(SymbolTable parent) {
    this.parent = Optional.of(parent);
    this.importDeclarations = new HashSet<String>();
    this.methodDeclarations = new HashMap<String, MethodDeclaration>(); 
    this.scalarDeclarations = new HashMap<String, ScalarDeclaration>(); 
    this.arrayDeclarations  = new HashMap<String, ArrayDeclaration>(); 
  }

  /**
   * Check whether an identifier exists in this symbol table's scope only. Does
   * not traverse parent symbol tables to find the symbols.
   *
   * @param identifier the symbol to find
   * @return whether a symbol of any kind with this identifier exists in the
   *  table's scopes
   */
  public boolean exists(String identifier) {
    return importDeclarations.contains(identifier)
      || methodDeclarations.containsKey(identifier) 
      || scalarDeclarations.containsKey(identifier)
      || arrayDeclarations.containsKey(identifier);
  }

  /**
   * Check whether an import exists in the symbol table of any scope enclosing
   * this table.
   *
   * @param identifier the import to find
   * @return whether this import exists in this or any enclosing symbol table
   */
  public boolean importExists(String identifier) {
    return importDeclarations.contains(identifier)
      || (!exists(identifier) && parent.isPresent() && parent.get().importExists(identifier));
  }

  /**
   * Check whether a method exists in the symbol table of any scope enclosing
   * this table.
   *
   * @param identifier the method to find
   * @return whether this method exists in this or any enclosing symbol table
   */
  public boolean methodExists(String identifier) {
    return methodDeclarations.containsKey(identifier)
      || (!exists(identifier) && parent.isPresent() && parent.get().methodExists(identifier));   
  }

  /**
   * Check whether a scalar exists in the symbol table of any scope enclosing
   * this table. Does not search for array variables.
   *
   * @param identifier the scalar to find
   * @return whether this scalar exists in this or any enclosing symbol table
   */
  public boolean scalarExists(String identifier) {
    return scalarDeclarations.containsKey(identifier) 
      || (!exists(identifier) && parent.isPresent() && parent.get().scalarExists(identifier));
  }

  /**
   * Check whether an array exists in the symbol table of any scope enclosing
   * this table. Does not search for scalar variables.
   *
   * @param identifier the array to find
   * @return whether this array exists in this or any enclosing symbol table
   */
  public boolean arrayExists(String identifier) {
    return arrayDeclarations.containsKey(identifier)
      || (!exists(identifier) && parent.isPresent() && parent.get().arrayExists(identifier));
  }

  /**
   * Add a new import to this symbol table.
   *
   * @param identifier the import's string identifier
   */
  public void addImport(String identifier) {
    importDeclarations.add(identifier);
  }

  /**
   * Add a new method to this symbol table.
   *
   * @param identifier the method's string identifier
   * @param returnType the method return type
   * @param argumentTypes the method's argument types, in the order they are
   *                      declared
   */
  public void addMethod(String identifier, MethodType returnType, List<VariableType> argumentTypes) {
    methodDeclarations.put(identifier, new MethodDeclaration(returnType, argumentTypes));
  }

  /**
   * Add a new scalar variable to this symbol table.
   *
   * @param identifier the scalar's string identifier
   * @param type the scalar type
   */
  public void addScalar(String identifier, VariableType type) {
    scalarDeclarations.put(identifier, new ScalarDeclaration(type));
  }

  /**
   * Add a new array variable to this symbol table.
   *
   * @param identifier the array's string identifier
   * @param type the array type
   */
  public void addArray(String identifier, VariableType type) {
    arrayDeclarations.put(identifier, new ArrayDeclaration(type));
  }

  /**
   * Retrieves the return type of a method with the given identifier.
   *
   * @param identifier the method name; must exist in the current symbol table
   *                   hierarchy
   * @return the corresponding method's return type
   */
  public MethodType methodReturnType(String identifier) {
    if (methodDeclarations.containsKey(identifier)) {
      return methodDeclarations.get(identifier).getReturnType();
    } else if (parent.isPresent()) {
      return parent.get().methodReturnType(identifier);
    } else {
      throw new RuntimeException("method not in symbol table");
    }
  }

  /**
   * Retrieve the argument types of a method with the given identifier.
   *
   * @param identifier the method name; must exist in the current symbol table
   *                   hierarchy
   * @return the corresponding method's argument types listed in the order they
   *  are declared
   */
  public List<VariableType> methodArgumentTypes(String identifier) {
    if (methodDeclarations.containsKey(identifier)) {
      return List.copyOf(methodDeclarations.get(identifier).argumentTypes);
    } else if (parent.isPresent()) {
      return parent.get().methodArgumentTypes(identifier);
    } else {
      throw new RuntimeException("method not in symbol table");
    }
  }

  /**
   * Retrieve the type of a scalar variable with the given identifier
   *
   * @param identifier the scalar name; must exist in the current symbol table
   *                   hierarchy
   * @return the corresponding scalar's type
   */
  public VariableType scalarType(String identifier) {
    if (scalarDeclarations.containsKey(identifier)) {
      return scalarDeclarations.get(identifier).getType();
    } else if (parent.isPresent()) {
      return parent.get().scalarType(identifier);
    } else {
      throw new RuntimeException("scalar not in symbol table");
    }
  }

  /**
   * Retrieve the type of an array variable with the given identifier
   *
   * @param identifier the array name; must exist in the current symbol table
   *                   hierarchy
   * @return the corresponding array's type
   */
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
