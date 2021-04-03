package edu.mit.compilers.ll;

import java.util.Optional;

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
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLocalScalarFieldDeclaration {\n");
    s.append(indent(depth+1) + "index: " + index+ ",\n");
    s.append("}\n");
    return s.toString();
  }

  @Override
  public String toString(){
    throw new UnsupportedOperationException("not implemented");
  }

}
