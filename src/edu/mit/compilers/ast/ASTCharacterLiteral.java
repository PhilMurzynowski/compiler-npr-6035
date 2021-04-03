package edu.mit.compilers.ast;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTCharacterLiteral implements ASTExpression {

  private final TextLocation textLocation;
  private final char value;

  public ASTCharacterLiteral(TextLocation textLocation, char value) {
    this.textLocation = textLocation;
    this.value = value;
  }

  public char getValue() {
    return this.value;
  }

  @Override
  public TextLocation getTextLocation() {
    return textLocation;
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTArgument.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(ASTExpression.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    return "'" + value + "'"; // FIXME(rbd): Handle un/escape characters.
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTCharacterLiteral {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "value: '" + value + "',\n"); // FIXME(rbd): Handle un/escape characters.
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  @Override
  public boolean equals(Object that) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int hashCode() {
    throw new RuntimeException("not implemented");
  }

}
