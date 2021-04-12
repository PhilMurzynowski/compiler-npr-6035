package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLStoreArrayCompoundStatement implements HLStoreStatement {
  
  public enum Type {
    ADD {
      @Override
      public BinaryExpressionType toBinaryExpressionType() {
        return BinaryExpressionType.ADD;
      }
    },
    SUBTRACT{
      @Override
      public BinaryExpressionType toBinaryExpressionType() {
        return BinaryExpressionType.SUBTRACT;
      }
    },
    INCREMENT{
      @Override
      public UnaryExpressionType toUnaryExpressionType() {
        return UnaryExpressionType.INCREMENT;
      }
    },
    DECREMENT{
      @Override
      public UnaryExpressionType toUnaryExpressionType() {
        return UnaryExpressionType.DECREMENT;
      }
    };

    public BinaryExpressionType toBinaryExpressionType() {
      throw new RuntimeException("Cannot convert unary expression to binary expression");
    }
    public UnaryExpressionType toUnaryExpressionType() {
      throw new RuntimeException("Cannot convert binary expression to unary expression");
    }
  }

  private final HLArrayFieldDeclaration declaration; 
  private final HLExpression index;
  private final Type type;
  private final Optional<HLExpression> expression;

  public HLStoreArrayCompoundStatement(HLArrayFieldDeclaration declaration, HLExpression index, Type type, Optional<HLExpression> expression) {
    this.declaration = declaration;
    this.index = index;
    this.type = type;
    this.expression = expression;
  }

  public HLArrayFieldDeclaration getDeclaration() {
    return declaration;
  }

  public HLExpression getIndex() {
    return index;
  }

  public Type getType() {
    return type;
  }

  public Optional<HLExpression> getExpression() {
    return expression;
  }


  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLStoreArrayCompoundStatement {\n");
    //s.append(indent(depth + 1) + "declaration: " + declaration.debugString(depth + 1) + ",\n");
    //s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    //s.append(indent(depth + 1) + "type: " + type + ",\n");
    //s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
