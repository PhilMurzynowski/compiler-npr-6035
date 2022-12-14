package edu.mit.compilers.ll;

import java.util.Optional;
// import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLAliasDeclaration implements LLDeclaration {

  private final int index;
  private Optional<Integer> stackIndex;

  public LLAliasDeclaration(int index) {
    this.index = index;
    stackIndex = Optional.empty();
  }

  public int getIndex() {
    return index;
  }

  public void setStackIndex(int stackIndex) {
    if (this.stackIndex.isPresent()) {
      throw new RuntimeException("stackIndex has already been set");
    } else {
      this.stackIndex = Optional.of(stackIndex);
    }
  }

  // NOTE(phil): change with register allocation
  @Override
  public String toUniqueDeclarationString() {
    return "%" + index;
  }

  @Override
  public String prettyString(int depth) {
    return "%" + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "declare %" + index;
  }

  @Override
  public String location() {
    if (this.stackIndex.isEmpty()) {
      throw new RuntimeException("stackIndex has not been set for alias with index " + index);
    } else {
      return stackIndex.get() + "(%rbp)";
    }
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLAliasDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    if (stackIndex.isPresent()) {
      s.append(indent(depth + 1) + "stackIndex: " + stackIndex.get() + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // private boolean sameValue(LLAliasDeclaration that) {
  //   return index == that.index
  //     && stackIndex.equals(that.stackIndex);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLAliasDeclaration && sameValue((LLAliasDeclaration)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(index, stackIndex);
  // }

}
