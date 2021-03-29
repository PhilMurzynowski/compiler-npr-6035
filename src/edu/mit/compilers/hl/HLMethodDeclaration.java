package edu.mit.compilers.hl;

import java.util.Optional;

import edu.mit.compilers.ll.*;

import static edu.mit.compilers.common.Utilities.indent;

// DONE: Noah
public class HLMethodDeclaration implements HLNode {

  private final String identifier;
  private final HLBlock body;
  private Optional<LLMethodDeclaration> ll;

  public HLMethodDeclaration(
    final String identifier,
    final HLBlock body)
  {
    this.identifier = identifier;
    this.body = body;
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
    s.append(indent(depth) + "HLMethodDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "body: " + body.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
