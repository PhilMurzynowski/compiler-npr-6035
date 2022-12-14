package edu.mit.compilers.ast;

import java.util.List;
import java.util.ArrayList;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class ASTBlock implements ASTNode {

  private final TextLocation textLocation;
  private final List<ASTFieldDeclaration> fieldDeclarations;
  private final List<ASTStatement> statements;

  private ASTBlock(TextLocation textLocation, List<ASTFieldDeclaration> fieldDeclarations, List<ASTStatement> statements) {
    this.textLocation = textLocation;
    this.fieldDeclarations = fieldDeclarations;
    this.statements = statements;
  }

  public static class Builder {

    private final TextLocation textLocation;
    private final List<ASTFieldDeclaration> fieldDeclarations;
    private final List<ASTStatement> statements;
    
    public Builder(TextLocation textLocation) {
      this.textLocation = textLocation;
      fieldDeclarations = new ArrayList<>();
      statements = new ArrayList<>();
    }

    public Builder addFieldDeclaration(ASTFieldDeclaration fieldDeclaration) {
      fieldDeclarations.add(fieldDeclaration);
      return this;
    }

    public Builder addStatement(ASTStatement statement) {
      statements.add(statement);
      return this;
    }

    public ASTBlock build() {
      return new ASTBlock(textLocation, List.copyOf(fieldDeclarations), List.copyOf(statements));
    }

  }

  public List<ASTFieldDeclaration> getFieldDeclarations() {
    return fieldDeclarations;
  }

  public List<ASTStatement> getStatements() {
    return statements;
  }

  @Override
  public TextLocation getTextLocation() {
    return textLocation;
  }

  @Override
  public <T> T accept(ASTNode.Visitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("{\n");
    for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
      s.append(indent(depth + 1) + fieldDeclaration.prettyString(depth + 1) + "\n");
    }
    for (ASTStatement statement : statements) {
      s.append(indent(depth + 1) + statement.prettyString(depth + 1) + "\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("ASTBlock {\n");
    s.append(indent(depth + 1) + "textLocation: " + textLocation.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "fieldDeclarations: [\n");
    for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
      s.append(indent(depth + 2) + fieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "statements: [\n");
    for (ASTStatement statement : statements) {
      s.append(indent(depth + 2) + statement.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  @Override
  public boolean equals(Object that) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public int hashCode() {
    throw new RuntimeException("not implemented");
  }

}
