package edu.mit.compilers.ll;

import java.util.Optional;

public class LLLocalArrayFieldDeclaration implements LLArrayFieldDeclaration {

  private final int index;
  private final int length;
  private Optional<Integer> stackIndex;

  public LLLocalArrayFieldDeclaration(int index, int length) {
    this.stackIndex = Optional.empty();
    throw new RuntimeException("not implemented");
  }

  public void setStackIndex(int stackIndex) {
    if (this.stackIndex.isPresent()) {
      throw new RuntimeException("stackIndex has already been set");
    } else {
      this.stackIndex = Optional.of(stackIndex);
    }
  }

  public int getLength() {
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
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }
}

