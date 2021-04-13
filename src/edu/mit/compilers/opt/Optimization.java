package edu.mit.compilers.opt;

import edu.mit.compilers.ll.LLControlFlowGraph;
import edu.mit.compilers.ll.LLDeclaration;
import edu.mit.compilers.ll.LLMethodDeclaration;

import java.util.List;

public interface Optimization {

  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals);

}
