package edu.mit.compilers.ast;

import java.util.Optional;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTLocationExpression implements ASTExpression {

  private final TextLocation textLocation;
  private final String identifier;
  private final Optional<ASTExpression> offset;

  private ASTLocationExpression(TextLocation textLocation, String identifier, Optional<ASTExpression> offset) {
    this.textLocation = textLocation;
    this.identifier = identifier;
    this.offset = offset;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private String identifier;
    private Optional<ASTExpression> offset;

    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      identifier = null;
      offset = Optional.empty();
    }

    public Builder withIdentifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder withOffset(ASTExpression offset) {
      this.offset = Optional.of(offset);
      return this;
    }

    public ASTLocationExpression build() {
      assert identifier != null;

      return new ASTLocationExpression(textLocation, identifier, offset);
    }
  }

  public String getIdentifier() {
    return identifier;
  }

  public Optional<ASTExpression> getOffset() {
    return offset;
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
    StringBuilder s = new StringBuilder();
    s.append(identifier);
    if (offset.isPresent()) {
      s.append("[");
      s.append(offset.get().prettyString(depth));
      s.append("]");
    }
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTLocationExpression {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    if (offset.isPresent()) {
      s.append(indent(depth + 1) + "offset: " + offset.get().debugString(depth + 1) + ",\n");
    }
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
