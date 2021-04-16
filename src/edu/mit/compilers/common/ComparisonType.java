package edu.mit.compilers.common;

public enum ComparisonType {
  EQUAL {
    @Override
    public String prettyString(int depth) {
      return "eq";
    }
  },
  NOT_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "ne";
    }
  },
  LESS_THAN {
    @Override
    public String prettyString(int depth) {
      return "lt";
    }
  },
  LESS_THAN_OR_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "le";
    }
  },
  GREATER_THAN {
    @Override
    public String prettyString(int depth) {
      return "gt";
    }
  },
  GREATER_THAN_OR_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "ge";
    }
  };

  public abstract String prettyString(int depth);
}
