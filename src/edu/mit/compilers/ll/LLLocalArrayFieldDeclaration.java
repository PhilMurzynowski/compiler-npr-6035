package edu.mit.compilers.ll;

import java.util.Optional;
// import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLocalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final int index;
  private final long length;
  private Optional<Integer> stackIndex;

  public LLLocalArrayFieldDeclaration(int index, long length) {
    this.index = index;
    this.length = length;
    this.stackIndex = Optional.empty();
  }

  public void setStackIndex(int stackIndex) {
    if (this.stackIndex.isPresent()) {
      throw new RuntimeException("stackIndex has already been set");
    } else {
      this.stackIndex = Optional.of(stackIndex);
    }
  }

  @Override
  public long getLength() {
    return length;
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
  public String index(String register) {
    if (this.stackIndex.isEmpty()) {
      throw new RuntimeException("stackIndex has not been set");
    } else {
      return stackIndex.get() + "(%rbp," + register + ",8)";
    }
  }

  @Override
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need array declaration as string");
  }

  @Override
  public String prettyString(int depth) {
    return "local arr " + index;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "local arr " + index + " = [" + length + " x i64]";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLocalArrayFieldDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "length: " + length + ",\n");
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

  // private boolean sameValue(LLLocalArrayFieldDeclaration that) {
  //   return index == that.index
  //     && length == that.length
  //     && stackIndex.equals(that.stackIndex);
  // }

  // @Override
  // public boolean equals(Object that) {
  //   return that instanceof LLLocalArrayFieldDeclaration && sameValue((LLLocalArrayFieldDeclaration)that);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hash(index, length, stackIndex);
  // }

}

