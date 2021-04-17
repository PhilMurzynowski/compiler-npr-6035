package edu.mit.compilers.common;

public enum ComparisonType {
  EQUAL {
    @Override
    public String prettyString(int depth) {
      return "eq";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.EQUAL;
    }
  },
  NOT_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "ne";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.NOT_EQUAL;
    }
  },
  LESS_THAN {
    @Override
    public String prettyString(int depth) {
      return "lt";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.LESS_THAN;
    }
  },
  LESS_THAN_OR_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "le";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.LESS_THAN_OR_EQUAL;
    }
  },
  GREATER_THAN {
    @Override
    public String prettyString(int depth) {
      return "gt";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.GREATER_THAN;
    }
  },
  GREATER_THAN_OR_EQUAL {
    @Override
    public String prettyString(int depth) {
      return "ge";
    }
    @Override
    public BinaryExpressionType toBinaryExpressionType() {
      return BinaryExpressionType.GREATER_THAN_OR_EQUAL;
    }
  };

  public abstract String prettyString(int depth);
  public abstract BinaryExpressionType toBinaryExpressionType();
}
