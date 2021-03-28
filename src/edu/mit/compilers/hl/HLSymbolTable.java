package edu.mit.compilers.hl;

import java.util.Optional;
import java.util.Map;
import java.util.List;

public class HLSymbolTable {

  private final Optional<HLSymbolTable> parent;

  private final Map<String, HLImportDeclaration> importDeclarations;
  private final Map<String, HLStringLiteralDeclaration> stringLiteralDeclaration;
  private final Map<String, HLScalarFieldDeclaration> scalarFieldDeclarations;
  private final Map<String, HLArrayFieldDeclaration> arrayFieldDeclarations;
  private final Map<String, HLMethodDeclaration> methodDeclarations;

  public HLSymbolTable() {
    throw new RuntimeException("not implemented");
  }

  public HLSymbolTable(HLSymbolTable parent) {
    throw new RuntimeException("not implemented");
  }

  public void addImport(String identifier, HLImportDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public void addStringLiteral(String value, HLStringLiteralDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public void addScalar(String identifier, HLScalarFieldDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public void addArray(String identifier, HLArrayFieldDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public void addMethod(String identifier, HLMethodDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public HLImportDeclaration getImport(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public HLStringLiteralDeclaration getStringLiteral(String value) {
    throw new RuntimeException("not implemented");
  }

  public List<HLStringLiteralDeclaration> getStringLiterals() {
    throw new RuntimeException("not implemented");
  }

  public HLScalarFieldDeclaration getScalar(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public HLArrayFieldDeclaration getArray(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public HLMethodDeclaration getMethod(String identifier) {
    throw new RuntimeException("not implemented");
  }

  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
