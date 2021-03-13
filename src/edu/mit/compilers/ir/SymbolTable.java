package edu.mit.compilers.ir;

import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.List;

import edu.mit.compilers.common.*;

class SymbolTable {

  private final Optional<SymbolTable> parent;

  private final Set<String> importDeclarations;
  private final Map<String, MethodDeclaration> methodDeclarations;
  private final Map<String, ScalarDeclaration> scalarDeclarations;
  private final Map<String, ArrayDeclaration> arrayDeclarations;

  // Noah
  private static class MethodDeclaration {

    private final MethodType returnType;
    private final List<VariableType> argumentTypes;

    public MethodDeclaration(MethodType returnType, List<VariableType> argumentTypes) {
      throw new RuntimeException("not implemented"); // FIXME(rbd): @nmpauls I didn't do a defensive copy in addMethod, figuring you might do one here instead?
    }

    public MethodType getReturnType() {
      return returnType;
    }

  }

  // Phil
  private static class ScalarDeclaration {

    private final VariableType type;

    public ScalarDeclaration(VariableType type) {
      throw new RuntimeException("not implemented");
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

  }

  // Noah
  public SymbolTable() {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public SymbolTable(SymbolTable parent) {
    throw new RuntimeException("not implemented");
  }

  public boolean exists(String identifier) {
    return importDeclarations.contains(identifier)
      || methodDeclarations.containsKey(identifier) 
      || scalarDeclarations.containsKey(identifier)
      || arrayDeclarations.containsKey(identifier);
  }

  // Noah
  public boolean importExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public boolean methodExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public boolean scalarExists(String identifier) {
    return scalarDeclarations.containsKey(identifier) 
      || (parent.isPresent() && parent.get().scalarExists(identifier));
  }

  // Noah
  public boolean arrayExists(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public void addImport(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public void addMethod(String identifier, MethodType returnType, List<VariableType> argumentTypes) {
    methodDeclarations.put(identifier, new MethodDeclaration(returnType, argumentTypes));
  }

  // Noah
  public void addScalar(String identifier, VariableType type) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public void addArray(String identifier, VariableType type) {
    throw new RuntimeException("not implemented");
  }

  public MethodType methodReturnType(String identifier) {
    return methodDeclarations.get(identifier).getReturnType();
  }

  // Noah
  public List<VariableType> methodArgumentTypes(String identifier) {
    throw new RuntimeException("not implemented");
  }

  // Phil
  public VariableType scalarType(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public VariableType arrayType(String identifier) {
    return arrayDeclarations.get(identifier).getType();
  }

}
