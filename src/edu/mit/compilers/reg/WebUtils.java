package edu.mit.compilers.reg;

import edu.mit.compilers.ll.LLBasicBlock;
import edu.mit.compilers.ll.LLControlFlowGraph;
import edu.mit.compilers.ll.LLInstruction;
import edu.mit.compilers.ll.LLMethodDeclaration;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class WebUtils {

  public static Set<String> getAllDefColors(LLMethodDeclaration methodDeclaration) {

    return getAllDefColors(methodDeclaration.getBody());

  }

  private static Set<String> getAllDefColors(LLControlFlowGraph cfg) {
    Set<String> colors = new HashSet<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    workSet.add(cfg.getEntry());

    while (!workSet.isEmpty()) {
      final LLBasicBlock currentBlock = workSet.iterator().next();
      workSet.remove(currentBlock);
      visited.add(currentBlock);

      colors.addAll(getAllDefColors(currentBlock));

      if (currentBlock.hasTrueTarget()) {
        final LLBasicBlock trueTarget = currentBlock.getTrueTarget();
        if (!visited.contains(trueTarget)) workSet.add(trueTarget);
      }
      if (currentBlock.hasFalseTarget()) {
        final LLBasicBlock falseTarget = currentBlock.getFalseTarget();
        if (!visited.contains(falseTarget)) workSet.add(falseTarget);
      }
    }

    return colors;

  }

  private static Set<String> getAllDefColors(LLBasicBlock bb) {
    Set<String> colors = new HashSet<>();

    for (LLInstruction instruction : bb.getInstructions()) {
      if (instruction.definition().isPresent() && instruction.defInRegister()) {
        colors.add(instruction.getDefWebLocation());
      }
    }

    return colors;
  }
}
