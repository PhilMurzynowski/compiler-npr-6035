package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

// DONE: Noah
public class HLMethodDeclaration implements HLNode {

  private final String identifier;
  private final HLBlock body;
  private final MethodType type;
  private Optional<LLMethodDeclaration> ll;

  public HLMethodDeclaration(
    final String identifier,
    final MethodType type,
    final HLBlock body)
  {
    this.identifier = identifier;
    this.type = type;
    this.body = body;
    ll = Optional.empty();
  }

  public MethodType getMethodType() {
    return type;
  }

  public void setLL(LLMethodDeclaration ll) {
    if (this.ll.isPresent()) {
      throw new RuntimeException("ll has already been set");
    } else {
      this.ll = Optional.of(ll);
    }
  }

  public LLMethodDeclaration getLL() {
    if (ll.isPresent()) {
      return ll.get();
    }
    throw new RuntimeException("ll is empty");
  }

  public String getIdentifier() {
    return identifier;
  }

  public HLBlock getBody() {
    return body;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLMethodDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
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
