package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class LLLoadArray implements LLInstruction {

  private final LLArrayFieldDeclaration location;
  private final LLDeclaration index;
  private final LLDeclaration result;

  public LLLoadArray(LLArrayFieldDeclaration location, LLDeclaration index, LLDeclaration result) {
    this.location = location;
    this.index = index;
    this.result = result;
  }

  public LLArrayFieldDeclaration getLocation() {
    return location;
  }

  public LLDeclaration getIndex() {
    return index;
  }

  public LLDeclaration getResult() {
    return result;
  }

  @Override
  public List<LLDeclaration> uses() {
    return List.of(location, index);
  }

  @Override
  public Optional<LLDeclaration> definition() {
    return Optional.of(result);
  }

  @Override
  public LLInstruction usesReplaced(List<LLDeclaration> uses) {
    return new LLLoadArray((LLArrayFieldDeclaration)uses.get(0), uses.get(1), result);
  }

  @Override
  public String getUniqueExpressionString() {
    throw new RuntimeException("no expression available");
  }

  @Override
  public String prettyString(int depth) {
    return result.prettyString(depth) + " = load " + location.prettyString(depth) + ", " + index.prettyString(depth);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLLoadArray {\n");
    s.append(indent(depth + 1) + "location: " + location.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "index: " + index.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "result: " + result.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  private boolean sameValue(LLLoadArray that) {
    return location.equals(that.location)
      && index.equals(that.index)
      && result.equals(that.result);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof LLLoadArray && sameValue((LLLoadArray)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, index, result);
  }

}
