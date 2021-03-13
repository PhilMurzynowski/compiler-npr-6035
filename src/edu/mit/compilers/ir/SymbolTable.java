package edu.mit.compilers.ir;

import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.List;

import edu.mit.compilers.common.*;

class SymbolTable {

  private final Optional<SymbolTable> parent;

  private final Set<String> importDeclarations;
  private final Map<String, MethodDeclaration> methodDeclartions;
  private final Map<String, ScalarDeclaration> scalarDeclarations;
  private final Map<String, ArrayDeclaration> arrayDeclarations;

  // Noah
  private static class MethodDeclaration {

    private final MethodType returnType;
    private final List<VariableType> argumentTypes;

    public MethodDeclaration(MethodType returnType, List<VariableType> argumentTypes) {
      throw new RuntimeException("not implemented");
    }

  }

  // Phil
  private static class ScalarDeclaration {

    private final VariableType type;

    public ScalarDeclaration(VariableType type) {
      throw new RuntimeException("not implemented");
    }

  }

  // Robert
  private static class ArrayDeclaration {

    private final VariableType type;

    public ArrayDeclaration(VariableType type) {
      throw new RuntimeException("not implemented");
    }

  }

  // Noah
  public SymbolTable() {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public SymbolTable(SymbolTable parent) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public boolean exists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public boolean importExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public boolean methodExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public boolean scalarExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public boolean arrayExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public void addImport(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public void addMethod(String identifier, MethodType returnType, List<VariableType> argumentTypes) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public void addScalar(String identifier, VariableType type) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public void addArray(String identifier, VariableType type) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public VariableType methodReturnType(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Noah
  public List<VariableType> methodArgumentTypes(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public VariableType scalarType(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Robert
  public VariableType arrayType(String identifier) {
    throw new RuntimeException("not implemented");
  }

}
