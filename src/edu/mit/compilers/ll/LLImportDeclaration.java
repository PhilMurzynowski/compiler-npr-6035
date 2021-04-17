package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLImportDeclaration implements LLDeclaration {

  private final String identifier;

  public LLImportDeclaration(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String location() {
    return identifier;
  }

  @Override
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need import declaration as string");
  }

  @Override
  public String prettyString(int depth) {
    return "@" + identifier;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    return "declare i64 @" + identifier + "(...)";
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLImportDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
