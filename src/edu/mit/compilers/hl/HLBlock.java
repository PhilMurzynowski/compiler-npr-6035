package edu.mit.compilers.hl;

import java.util.List;
import java.util.ArrayList;

import static edu.mit.compilers.common.Utilities.indent;

public class HLBlock implements HLNode {

  private final List<HLArgumentDeclaration> argumentDeclarations;
  private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStatement> statements;

  public HLBlock( 
    final List<HLArgumentDeclaration> argumentDeclarations,
    final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations,
    final List<HLLocalArrayFieldDeclaration> arrayFieldDeclarations,
    final List<HLStatement> statements)
  {
    this.argumentDeclarations = argumentDeclarations;
    this.scalarFieldDeclarations = scalarFieldDeclarations;
    this.arrayFieldDeclarations = arrayFieldDeclarations;
    this.statements = statements;
  }

  public static class Builder {

    private final List<HLArgumentDeclaration> argumentDeclarations;
    private final List<HLLocalScalarFieldDeclaration> scalarFieldDeclarations;
    private final List<HLLocalArrayFieldDeclaration> arrayFieldDeclarations;
    private final List<HLStatement> statements;

    public Builder() {
      argumentDeclarations = new ArrayList<>();
      scalarFieldDeclarations = new ArrayList<>();
      arrayFieldDeclarations = new ArrayList<>();
      statements = new ArrayList<>();
    }

    public Builder addArgument(HLArgumentDeclaration argument) {
      argumentDeclarations.add(argument);
      return this;
    }

    public int argumentIndex() {
      return argumentDeclarations.size();
    }

    public Builder addScalar(HLLocalScalarFieldDeclaration scalar) {
      scalarFieldDeclarations.add(scalar);
      return this;
    }

    public int scalarIndex() {
      return scalarFieldDeclarations.size();
    }

    public Builder addArray(HLLocalArrayFieldDeclaration array) {
      arrayFieldDeclarations.add(array);
      return this;
    }

    public int arrayIndex() {
      return arrayFieldDeclarations.size();
    }

    public Builder addStatement(HLStatement statement) {
      statements.add(statement);
      return this;
    }

    public HLBlock build() {
      return new HLBlock(List.copyOf(argumentDeclarations), List.copyOf(scalarFieldDeclarations), List.copyOf(arrayFieldDeclarations), List.copyOf(statements));
    }

  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("HLBlock {\n");
    s.append(indent(depth + 1) + "argumentDeclarations: [\n");
    for (HLArgumentDeclaration argumentDeclaration : argumentDeclarations) {
      s.append(indent(depth + 2) + argumentDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "scalarFieldDeclarations: [\n");
    for (HLLocalScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth + 2) + scalarFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "arrayFieldDeclarations: [\n");
    for (HLLocalArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth + 2) + arrayFieldDeclaration.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth + 1) + "statements: [\n");
    for (HLStatement statement : statements) {
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

}
