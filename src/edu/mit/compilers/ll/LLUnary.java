package edu.mit.compilers.ll;

import java.util.*;

import edu.mit.compilers.common.*;
import edu.mit.compilers.reg.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLUnary implements LLInstruction {

  private final UnaryExpressionType type;
  private final LLDeclaration expression;
  private final LLDeclaration result;
  private Optional<Web> definitionWeb;
  private Map<LLDeclaration, Web> usesWebs;

  public LLUnary(UnaryExpressionType type, LLDeclaration expression, LLDeclaration result) {
    this.type = type;
    this.expression = expression;
    this.result = result;
    this.definitionWeb = Optional.empty();
    this.usesWebs = new HashMap<>();
  }

  public UnaryExpressionType getType() {
    return type;
  }

  public LLDeclaration getExpression() {
    return expression;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public void setDefinitionWeb(final Web web) {
    if (definitionWeb.isPresent()) {
      throw new RuntimeException("definitionWeb has already been set");
    } else {
      definitionWeb = Optional.of(web);
    }
  }

  @Override
  public void addUsesWeb(final LLDeclaration definition, final Web web) {
    usesWebs.put(definition, web);
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(expression);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLUnary(type, uses.get(0), result);
  }

  @Override
  public boolean defInRegister() {
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      return !webLocation.equals(Web.SPILL);
    } else {
      return false;
    }
  }

  @Override
  public boolean useInRegister(LLDeclaration use) {
    assert uses().contains(use) : "use must be expression";
    if (usesWebs.containsKey(use)) {
      final Web useWeb = usesWebs.get(use);
      final String webLocation = useWeb.getLocation();
      return !webLocation.equals(Web.SPILL);
    } else {
      return false;
    }
  }

  @Override
  public String getDefWebLocation() {
    if (definitionWeb.isPresent()) {
      final String webLocation = definitionWeb.get().getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return definition().get().location();
      } else {
        return webLocation;
      }
    } else {
      return definition().get().location();
    }
  }

  @Override
  public String getUseWebLocation(LLDeclaration use) {
    assert uses().contains(use) : "use not in uses";
    if (usesWebs.containsKey(use)) {
      final Web useWeb = usesWebs.get(use);
      final String webLocation = useWeb.getLocation();
      if (webLocation.equals(Web.SPILL)) {
        return use.location();
      } else {
        return webLocation;
      }
    } else {
      return use.location();
    }
  }

  @Override
  public String getUniqueExpressionString() {
    StringBuilder exprBuilder =  new StringBuilder();

    if (this.type == UnaryExpressionType.NOT || this.type ==  UnaryExpressionType.NEGATE) {
      exprBuilder.append(type.toString());
      exprBuilder.append(expression.toUniqueDeclarationString());
    } else if (this.type == UnaryExpressionType.INCREMENT || this.type == UnaryExpressionType.DECREMENT) {
      exprBuilder.append(expression.toUniqueDeclarationString());
      exprBuilder.append(type.toString());
    } else {
      throw new RuntimeException("unreachable\n");
    }

    return exprBuilder.toString();
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(result.prettyString(depth) + " = ");

    if (type.equals(UnaryExpressionType.NOT)) {
      s.append("not");
    } else if (type.equals(UnaryExpressionType.NEGATE)) {
      s.append("neg");
    } else if (type.equals(UnaryExpressionType.INCREMENT)) {
      s.append("inc");
    } else if (type.equals(UnaryExpressionType.DECREMENT)) {
      s.append("dec");
    } else {
      throw new RuntimeException("unreachable");
    }

    s.append(" " + expression.prettyString(depth));

    s.append(" ".repeat(32 - depth * 2 - s.length()) + "; webs { ");
    if (definitionWeb.isPresent()) {
      s.append(result.prettyString(depth) + " => (" + definitionWeb.get().getIndex() + ", " + definitionWeb.get().getLocation() + "), ");
    }
    for (final Map.Entry<LLDeclaration, Web> entry : usesWebs.entrySet()) {
      s.append(entry.getKey().prettyString(depth) + " => (" + entry.getValue().getIndex() + ", " + entry.getValue().getLocation() + "), ");
    }
    s.append("}");

    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLUnary {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "expression: " + expression.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    if (definitionWeb.isPresent()) {
      s.append(indent(depth + 1) + "definitionWeb: " + definitionWeb.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth + 1) + "usesWebs: {\n");
    for (final Map.Entry<LLDeclaration, Web> entry : usesWebs.entrySet()) {
      s.append(indent(depth + 2) + entry.getKey().debugString(depth + 2) + " => " + entry.getValue().debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "},\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLUnary that) {
  //   return type.equals(that.type)
  //     && expression.equals(that.expression)
  //     && result.equals(that.result);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLUnary && sameValue((LLUnary)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(type, expression, result);
  // }

}
