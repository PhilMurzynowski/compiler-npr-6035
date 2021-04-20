package edu.mit.compilers.opt;

import edu.mit.compilers.common.MethodType;
import edu.mit.compilers.ll.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UnusedLocalElimination implements Optimization{

  @Override
  public void apply(LLMethodDeclaration methodDeclaration, LLControlFlowGraph controlFlowGraph, List<LLDeclaration> globals) {
    eliminateMethod(methodDeclaration);
  }

  /**
   * Eliminate all unused local variables from a method.
   */
  public static void eliminateMethod(LLMethodDeclaration method) {
    Set<LLDeclaration> usedLocals = findUsedLocals(method.getBody());
    // void methods always use alias 0 in return, but this is not captured
    // by current usage of LLReturn
    if (method.getMethodType() == MethodType.VOID) {
      usedLocals.add(method.getAliasDeclarations().get(0));
    }

    // remove unused aliases
    final Set<LLAliasDeclaration> unusedAliases = new HashSet<>();
    for (LLAliasDeclaration alias : method.getAliasDeclarations()) {
      if (!usedLocals.contains(alias)) {
        unusedAliases.add(alias);
      }
    }
    for (LLAliasDeclaration alias : unusedAliases)
      method.removeAlias(alias);

    // remove unused scalars
    final Set<LLLocalScalarFieldDeclaration> unusedScalars = new HashSet<>();
    for (LLLocalScalarFieldDeclaration scalar : method.getScalarFieldDeclarations()) {
      if (!usedLocals.contains(scalar)) {
        unusedScalars.add(scalar);
      }
    }
    for (LLLocalScalarFieldDeclaration scalar : unusedScalars)
      method.removeScalar(scalar);

    // remove unused arrays
    final Set<LLLocalArrayFieldDeclaration> unusedArrays = new HashSet<>();
    for (LLLocalArrayFieldDeclaration array : method.getArrayFieldDeclarations()) {
      if (!usedLocals.contains(array)) {
        unusedArrays.add(array);
      }
    }
    for (LLLocalArrayFieldDeclaration array : unusedArrays)
      method.removeArray(array);

    /*
    // Display all removed local variables and aliases

    System.err.println("+-------------------------");
    System.err.println("| " + method.getIdentifier());
    System.err.println("|  removed aliases: " + unusedAliases.size());
    for (LLAliasDeclaration a : unusedAliases) {
      System.err.println("|    " + a.prettyStringDeclaration(0));
    }
    System.err.println("|  removed scalars: " + unusedScalars.size());
    for (LLLocalScalarFieldDeclaration s : unusedScalars) {
      System.err.println("|    " + s.prettyStringDeclaration(0));
    }
    System.err.println("|  removed arrays: " + unusedArrays.size());
    for (LLLocalArrayFieldDeclaration a : unusedArrays) {
      System.err.println("|    " + a.prettyStringDeclaration(0));
    }
    System.err.println("+-------------------------\n");
    */
  }

  /**
   * Find all the locals used in a control flow graph.
   */
  public static Set<LLDeclaration> findUsedLocals(LLControlFlowGraph methodBody) {
    final Set<LLBasicBlock> visited = new HashSet<>();
    Set<LLBasicBlock> onDeck = new HashSet<>();
    onDeck.add(methodBody.getEntry());

    while (onDeck.size() > 0) {
      Set<LLBasicBlock> next = new HashSet<>();
      visited.addAll(onDeck);
      for (LLBasicBlock block : onDeck) {
        for (LLBasicBlock nextBlock : block.getSuccessors()) {
          if (!visited.contains(nextBlock)) {
            next.add(nextBlock);
          }
        }
      }
      onDeck = next;
    }

    final Set<LLDeclaration> usedLocals = new HashSet<>();

    for (LLBasicBlock block : visited) {
      for (LLInstruction instruction : block.getInstructions()) {
        usedLocals.addAll(instruction.uses());
      }
    }

    return usedLocals;
  }
}
