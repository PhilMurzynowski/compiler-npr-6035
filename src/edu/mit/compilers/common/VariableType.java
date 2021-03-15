package edu.mit.compilers.common;

public enum VariableType {

  INTEGER {

    @Override
    public MethodType toMethodType() {
      return MethodType.INTEGER;
    }

  },

  BOOLEAN {

    @Override
    public MethodType toMethodType() {
      return MethodType.BOOLEAN;
    }

  };

  public abstract MethodType toMethodType();

}
