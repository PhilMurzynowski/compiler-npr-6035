package edu.mit.compilers.ll;

import java.util.Optional;

public class LLAliasDeclaration implements LLDeclaration {

  private final int index;
  private Optional<Integer> stackIndex;

  public LLAliasDeclaration(int index) {
    stackIndex = Optional.empty();
    throw new RuntimeException("not implemented");
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
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
