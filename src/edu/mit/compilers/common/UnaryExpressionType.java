package edu.mit.compilers.common;

public enum UnaryExpressionType {
  NOT {
    @Override
    public String toString() {
      return "not";
    }
  },
  NEGATE {
    @Override
    public String toString() {
      return "neg";
    }
  },
  INCREMENT {
    @Override
    public String toString() {
      return "inc";
    }
  },
  DECREMENT {
    @Override
    public String toString() {
      return "dec";
    }
  };

  public abstract String toString();
}
