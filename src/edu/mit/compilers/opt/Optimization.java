package edu.mit.compilers.opt;

import edu.mit.compilers.ll.LLControlFlowGraph;
import edu.mit.compilers.ll.LLDeclaration;

import java.util.List;

public interface Optimization {

  public void apply(LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals);

}
