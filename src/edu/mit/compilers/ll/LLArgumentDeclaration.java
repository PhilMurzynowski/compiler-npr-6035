package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLArgumentDeclaration implements LLScalarFieldDeclaration {

  private final int index;
  private Optional<Integer> stackIndex;

  public LLArgumentDeclaration(int index) {
    this.index = index;
    stackIndex = Optional.empty();
  }

  @Override
  public String toUniqueDeclarationString() {
    return "arg " + index;
  }

  @Override
  public String prettyString(int depth) {
    return "arg " + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "declare arg " + index;
  }

  public void setStackIndex(int stackIndex) {
    if (this.stackIndex.isPresent()) {
      throw new RuntimeException("stackIndex has already been set");
    } else {
      this.stackIndex = Optional.of(stackIndex);
    }
  }

  @Override
  public String location() {
    if (index < 6) {
      if (this.stackIndex.isEmpty()) {
        throw new RuntimeException("stackIndex has not been set");
      } else {
        return stackIndex.get() + "(%rbp)";
      }
    } else {
      return (16 + (index - 6) * 8) + "(%rbp)";
    }
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLArgumentDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  private boolean sameValue(LLArgumentDeclaration that) {
    return index == that.index
      && stackIndex.equals(that.stackIndex);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLArgumentDeclaration && sameValue((LLArgumentDeclaration)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, stackIndex);
  }

}
