package edu.mit.compilers.ir;
import java.util.*;

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
      this.returnType = returnType;
      this.argumentTypes = new ArrayList<>(argumentTypes);
    }

    public MethodType getReturnType() {
      return returnType;
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
    return methodDeclarations.get(identifier).getReturnType();
  }

  public List<VariableType> methodArgumentTypes(String identifier) {
    return new ArrayList<>(methodDeclarations.get(identifier).argumentTypes);
  }

  public VariableType scalarType(String identifier) {
		return scalarDeclarations.get(identifier).getType();
  }

  public VariableType arrayType(String identifier) {
    return arrayDeclarations.get(identifier).getType();
  }

}
