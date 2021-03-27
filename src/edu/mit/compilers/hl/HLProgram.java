package edu.mit.compilers.hl;

import java.util.List;

class HLProgram implements HLNode {

  private final List<HLImportDeclaration> importDeclarations;
  private final List<HLGlobalScalarFieldDeclaration> scalarFieldDeclarations;
  private final List<HLGlobalArrayFieldDeclaration> arrayFieldDeclarations;
  private final List<HLStringDeclaration> stringDeclarations;
  private final List<HLMethodDeclaration> methodDeclarations;

}
