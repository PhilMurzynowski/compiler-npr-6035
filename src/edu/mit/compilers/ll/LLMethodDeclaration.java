package edu.mit.compilers.ll;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import edu.mit.compilers.common.*;
import edu.mit.compilers.opt.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLMethodDeclaration implements LLDeclaration {

  private final String identifier;
  private final MethodType type;
  private final List<LLArgumentDeclaration> argumentDeclarations;
  private final List<LLLocalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<LLLocalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<LLAliasDeclaration> aliasDeclarations;
  private Optional<LLBasicBlock> outOfBoundsExceptionBB;
  private Optional<LLControlFlowGraph> body;
  private int allocatedSpace;

  public LLMethodDeclaration(String identifier, MethodType type) {
    this.identifier = identifier;
    this.type = type;
    argumentDeclarations = new ArrayList<>();
    scalarFieldDeclarations = new ArrayList<>();
    arrayFieldDeclarations = new ArrayList<>();
    aliasDeclarations = new ArrayList<>();
    body = Optional.empty();
    this.outOfBoundsExceptionBB = Optional.empty();
    this.allocatedSpace = 0;
  }

  public MethodType getMethodType() {
    return type;
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

  public void removeScalar(LLLocalScalarFieldDeclaration scalar) {
    scalarFieldDeclarations.remove(scalar);
  }

  public int scalarIndex() {
    return scalarFieldDeclarations.size();
  }

  public void addArray(LLLocalArrayFieldDeclaration array) {
    arrayFieldDeclarations.add(array);
  }

  public void removeArray(LLArrayFieldDeclaration array) {
    arrayFieldDeclarations.remove(array);
  }

  public int arrayIndex() {
    return arrayFieldDeclarations.size();
  }

  private void addAlias(LLAliasDeclaration alias) {
    aliasDeclarations.add(alias);
  }

  public void removeAlias(LLAliasDeclaration alias) {
    aliasDeclarations.remove(alias);
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

  // NOTE(rbd): this is used in UnreachableCodeElimination
  public void replaceBody(LLControlFlowGraph body) {
    if (this.body.isEmpty()) {
      throw new RuntimeException("no body has been set");
    } else {
      this.body = Optional.of(body);
    }
  }

  public boolean hasBody() {
    return this.body.isPresent();
  }
  
  public LLControlFlowGraph getBody() {
    if (this.body.isPresent()) {
      return this.body.get();
    } else {
      throw new RuntimeException("no body has been set");
    }
  }

  public String getIdentifier() {
    return identifier;
  }

  public List<LLArgumentDeclaration> getArgumentDeclarations() {
    return this.argumentDeclarations;
  }

  public List<LLLocalScalarFieldDeclaration> getScalarFieldDeclarations() {
    return this.scalarFieldDeclarations;
  }

  public List<LLLocalArrayFieldDeclaration> getArrayFieldDeclarations() {
    return this.arrayFieldDeclarations;
  }

  public List<LLAliasDeclaration> getAliasDeclarations() {
    return this.aliasDeclarations;
  }

  public boolean hasOutOfBoundsExceptionBB() {
    return outOfBoundsExceptionBB.isPresent();
  }

  public LLBasicBlock getOutOfBoundsExceptionBB() {
    if (outOfBoundsExceptionBB.isEmpty()) {
      outOfBoundsExceptionBB = Optional.of(new LLBasicBlock(
        new LLException(LLException.Type.OutOfBounds)
      ));
    }
    return outOfBoundsExceptionBB.get();
  }

  public int setStackIndices() {
    // 48 for all callee saved registers
    int index = -48;

    for (int i = 0; i < argumentDeclarations.size() && i < 6; i++) {
      argumentDeclarations.get(i).setStackIndex(index);
      index -= 8;
    }

    for (LLLocalScalarFieldDeclaration scalar : scalarFieldDeclarations) {
      scalar.setStackIndex(index);
      index -= 8;
    }

    for (LLLocalArrayFieldDeclaration array : arrayFieldDeclarations) {
      index -= (array.getLength()) * 8;
      array.setStackIndex(index);
      index -= 8;
    }

    for (LLAliasDeclaration alias : aliasDeclarations) {
      alias.setStackIndex(index);
      index -= 8;
    }
    
    return -index - 48;
  }

  public void setAllocatedSpace(int allocatedSpace) {
    this.allocatedSpace = allocatedSpace;
  }

  public int getAllocatedSpace() {
    return this.allocatedSpace;
  }

  public void accept(Optimization optimization, List<LLDeclaration> globals) {
    optimization.apply(this, body.get(), globals);
  }

  @Override
  public String location() {
    return identifier;
  }

  @Override
  public String toUniqueDeclarationString() {
    throw new RuntimeException("Should not need method declaration as string");
  }

  @Override
  public String prettyString(int depth) {
    return "@" + identifier;
  }

  @Override
  public String prettyStringDeclaration(int depth) {
    StringBuilder s = new StringBuilder();

    s.append(identifier + ":\n");

    for (LLArgumentDeclaration argumentDeclaration : argumentDeclarations) {
      s.append(indent(depth + 1) + argumentDeclaration.prettyStringDeclaration(depth + 1) + "\n");
    }

    for (LLLocalScalarFieldDeclaration scalarFieldDeclaration : scalarFieldDeclarations) {
      s.append(indent(depth + 1) + scalarFieldDeclaration.prettyStringDeclaration(depth + 1) + "\n");
    }

    for (LLLocalArrayFieldDeclaration arrayFieldDeclaration : arrayFieldDeclarations) {
      s.append(indent(depth + 1) + arrayFieldDeclaration.prettyStringDeclaration(depth + 1) + "\n");
    }

    // for (LLAliasDeclaration aliasDeclaration : aliasDeclarations) {
    //   s.append(indent(depth + 1) + aliasDeclaration.prettyStringDeclaration(depth + 1) + ",\n");
    // }

    if (body.isPresent()) {
      s.append(indent(depth + 1) + body.get().prettyString(depth + 1));
    }

    return s.toString().strip();
  }

  @Override
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
    if (outOfBoundsExceptionBB.isPresent()) {
      s.append(indent(depth + 1) + "outOfBoundsExceptionBB: " + outOfBoundsExceptionBB.get().debugString(depth + 1) + ",\n");
    }
    if (body.isPresent()) {
      s.append(indent(depth + 1) + "body: " + body.get().debugString(depth + 1) + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  // NOTE(rbd): LLMethodDeclaration is mutable. Use default `.equals()` and `.hashCode()`

  // @Override
  // public boolean equals(Object that) {
  //   throw new RuntimeException("not implemented");
  // }

  // @Override
  // public int hashCode() {
  //   throw new RuntimeException("not implemented");
  // }

}
