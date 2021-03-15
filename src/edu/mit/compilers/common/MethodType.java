package edu.mit.compilers.common;

public enum MethodType {

  INTEGER {

    @Override
    public VariableType toVariableType() {
      return VariableType.INTEGER;
    }

  },

  BOOLEAN {

    @Override
    public VariableType toVariableType() {
      return VariableType.BOOLEAN;
    }

  },

  VOID {

    @Override
    public VariableType toVariableType() {
      throw new RuntimeException("Cannot convert MethodType.VOID to variableType");
    }
    
  };

  public abstract VariableType toVariableType();

}
