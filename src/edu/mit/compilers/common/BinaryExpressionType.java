package edu.mit.compilers.common;

public enum BinaryExpressionType {
  OR {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.OR to comparisonType");
    }
  },
  AND {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.AND to comparisonType");
    }
  },
  EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.EQUAL;
    }
  },
  NOT_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.NOT_EQUAL;
    }
  },
  LESS_THAN {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.LESS_THAN;
    }
  },
  LESS_THAN_OR_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.LESS_THAN_OR_EQUAL;
    }
  },
  GREATER_THAN {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.GREATER_THAN;
    }
  },
  GREATER_THAN_OR_EQUAL {
    @Override
    public ComparisonType toComparisonType() {
      return ComparisonType.GREATER_THAN_OR_EQUAL;
    }
  },
  ADD {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.ADD to comparisonType");
    }
  },
  SUBTRACT {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.SUBTRACT to comparisonType");
    }
  },
  MULTIPLY {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.MULTIPLY to comparisonType");
    }
  },
  DIVIDE {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.DIVIDE to comparisonType");
    }
  },
  MODULUS {
    @Override
    public ComparisonType toComparisonType() {
      throw new RuntimeException("Cannot convert BinaryExceptionType.MODULUS to comparisonType");
    }
  };

  public abstract ComparisonType toComparisonType();
}
