package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class LLMethodDeclaration implements LLDeclaration {

  private final String identifier;
  private final List<LLArgumentDeclaration> argumentDeclarations;
  private final List<LLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLAliasDeclaration> aliasDeclarations;
  private Optional<LLControlFlowGraph> body;

  public LLMethodDeclaration(String identifier) {
    this.identifier = identifier;
    argumentDeclarations = new ArrayList<>();
    scalarFieldDeclarations = new ArrayList<>();
    arrayFieldDeclarations = new ArrayList<>();
    aliasDeclarations = new ArrayList<>();
    body = Optional.empty();
  }

  public void addArgument(LLArgumentDeclaration argument) {
    argumentDeclarations.add(argument);
  }

  public int argumentIndex() {
    return argumentDeclarations.size();
  }

  public void addScalar(LLLocalScalarFieldDeclaration scalar) {
    scalarFieldDeclarations.add(scalar);
  }

  public int scalarIndex() {
    return scalarFieldDeclarations.size();
  }

  public void addArray(LLLocalArrayFieldDeclaration array) {
    arrayFieldDeclarations.add(array);
  }

  public int arrayIndex() {
    return arrayFieldDeclarations.size();
  }

  private void addAlias(LLAliasDeclaration alias) {
    aliasDeclarations.add(alias);
  }

  private int aliasIndex() {
    return aliasDeclarations.size();
  }

  public LLAliasDeclaration newAlias() {
    final LLAliasDeclaration alias = new LLAliasDeclaration(aliasIndex());
    addAlias(alias);
    return alias;
  }

  public void setBody(LLControlFlowGraph body) {
    if (this.body.isPresent()) {
      throw new RuntimeException("body has already been set");
    } else {
      this.body = Optional.of(body);
    }
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String location() {
    return identifier;
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLMethodDeclaration {\n");
    s.append(indent(depth + 1) + "identifier: " + identifier + ",\n");
    s.append(indent(depth + 1) + "argumentDeclarations: [\n");
    for (LLArgumentDeclaration argumentDeclaration : argumentDeclarations) {
      s.append(indent(depth + 2) + argumentDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "scalarFieldDeclarations: [\n");
    for (LLLocalScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth + 2) + scalarFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "arrayFieldDeclarations: [\n");
    for (LLLocalArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth + 2) + arrayFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "aliasDeclarations: [\n");
    for (LLAliasDeclaration aliasDeclaration : aliasDeclarations) {
      s.append(indent(depth + 2) + aliasDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "body: " + body.get().debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
