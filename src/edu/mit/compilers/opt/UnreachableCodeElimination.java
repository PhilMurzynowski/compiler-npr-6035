package edu.mit.compilers.opt;

import java.util.*;

import edu.mit.compilers.ll.*;

public class UnreachableCodeElimination implements Optimization {

  public void apply(final LLMethodDeclaration methodDeclaration, final LLControlFlowGraph controlFlowGraph, final List<LLDeclaration> globals) {
    methodDeclaration.replaceBody(controlFlowGraph.simplify(true));
  }

}
