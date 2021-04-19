package edu.mit.compilers.common;

public enum BinaryExpressionType {
  OR {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.OR to comparisonType");
    }
    @Override
    public String toString() {
      return "or";
    }
  },
  AND {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.AND to comparisonType");
    }
    @Override
    public String toString() {
      return "and";
    }
  },
  EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.EQUAL;
    }
    @Override
    public String toString() {
      return "eq";
    }
  },
  NOT_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.NOT_EQUAL;
    }
    @Override
    public String toString() {
      return "ne";
    }
  },
  LESS_THAN {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.LESS_THAN;
    }
    @Override
    public String toString() {
      return "lt";
    }
  },
  LESS_THAN_OR_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.LESS_THAN_OR_EQUAL;
    }
    @Override
    public String toString() {
      return "le";
    }
  },
  GREATER_THAN {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.GREATER_THAN;
    }
    @Override
    public String toString() {
      return "gt";
    }
  },
  GREATER_THAN_OR_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.GREATER_THAN_OR_EQUAL;
    }
    @Override
    public String toString() {
      return "ge";
    }
  },
  ADD {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.ADD to comparisonType");
    }
    @Override
    public String toString() {
      return "add";
    }
  },
  SUBTRACT {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.SUBTRACT to comparisonType");
    }
    @Override
    public String toString() {
      return "sub";
    }
  },
  MULTIPLY {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.MULTIPLY to comparisonType");
    }
    @Override
    public String toString() {
      return "mul";
    }
  },
  DIVIDE {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.DIVIDE to comparisonType");
    }
    @Override
    public String toString() {
      return "div";
    }
  },
  MODULUS {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.MODULUS to comparisonType");
    }
    @Override
    public String toString() {
      return "mod";
    }
  },
  SHIFT_LEFT {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.SHIFT_LEFT to comparisonType");
    }
    @Override
    public String toString() {
      return "shl";
    }
  },
  SHIFT_RIGHT {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.SHIFT_RIGHT to comparisonType");
    }
    @Override
    public String toString() {
      return "shr";
    }
  };

  public abstract ComparisonType toComparisonType();
  public abstract String toString();
}
