package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

import static edu.mit.compilers.common.Utilities.indent;

public class HLStringLiteralDeclaration implements HLNode {

  private static int count = 0;

  private final int index;
  private final String value;
  private Optional<LLStringLiteralDeclaration> ll;

  public HLStringLiteralDeclaration(final String value) {
    this.index = count++;
    this.value = value;
    ll = Optional.empty();
  }

  public int getIndex() {
    return index;
  }

  public String getValue() {
    return value;
  }

  public void setLL(LLStringLiteralDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  public LLStringLiteralDeclaration getLL() {
    if (this.ll.isEmpty()) {
      throw new RuntimeException("ll has not been set");
    } else {
      return this.ll.get();
    }
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLStringLiteralDeclaration {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    s.append(indent(depth + 1) + "value: " + value + ",\n");
    if (ll.isPresent()) {
      s.append(indent(depth + 1) + "ll: " + ll.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
