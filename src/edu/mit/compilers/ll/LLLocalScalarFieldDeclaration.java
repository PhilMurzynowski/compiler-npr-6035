package edu.mit.compilers.ll;

import java.util.Optional;
// import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLocalScalarFieldDeclaration implements LLScalarFieldDeclaration {

  private final int index;
  private Optional<Integer> stackIndex;

  public LLLocalScalarFieldDeclaration(int index) {
    this.index = index;
    stackIndex = Optional.empty();
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
    if (this.stackIndex.isEmpty()) {
      throw new RuntimeException("stackIndex has not been set");
    } else {
      return stackIndex.get() + "(%rbp)";
    }
  }

  @Override
  public String toUniqueDeclarationString() {
    return "local " + index;
  }

  @Override
  public String prettyString(int depth) {
    return "local " + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "local " + index + " = i64";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLocalScalarFieldDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index+ ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLLocalScalarFieldDeclaration that) {
  //   return index == that.index
  //     && stackIndex.equals(that.stackIndex);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLLocalScalarFieldDeclaration && sameValue((LLLocalScalarFieldDeclaration)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(index, stackIndex);
  // }

}
