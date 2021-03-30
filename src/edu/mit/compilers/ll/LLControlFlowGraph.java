package edu.mit.compilers.ll;

import static edu.mit.compilers.common.Utilities.indent;

public class LLControlFlowGraph implements LLNode {

  private final LLBasicBlock entry;
  private final LLBasicBlock exit;

  // NOTE(rbd): Please try not to add anything to this class. Let me know if you do. :)

  public LLControlFlowGraph(LLBasicBlock entry, LLBasicBlock exit) {
    this.entry = entry;
    this.exit = exit;
  }

  public static LLControlFlowGraph single(LLBasicBlock basicBlock) {
    return new LLControlFlowGraph(basicBlock, basicBlock);
  }

  public static LLControlFlowGraph empty() {
    return single(new LLBasicBlock());
  }

  public LLBasicBlock getEntry() {
    return entry;
  }

  public LLBasicBlock getExit() {
    return exit;
  }

  public LLControlFlowGraph concatenate(LLControlFlowGraph that) {
    this.exit.setTrueTarget(that.entry);
    return new LLControlFlowGraph(this.entry, that.exit);
  }

  public LLControlFlowGraph concatenate(LLBasicBlock basicBlock) {
    return concatenate(LLControlFlowGraph.single(basicBlock));
  }

  public LLControlFlowGraph concatenate(LLInstruction ...instructions) {
    return concatenate(LLControlFlowGraph.single(new LLBasicBlock(instructions)));
  }

  public LLControlFlowGraph simplify() {
    LLBasicBlock simplifiedEntry = entry.simplify();
    LLBasicBlock simplifiedExit = simplifiedEntry.getExit();

    return new LLControlFlowGraph(simplifiedEntry, simplifiedExit);
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append(indent(depth) + "LLControlFlowGraph {\n");
    s.append(indent(depth + 1) + "entry: " + entry.debugString(depth + 1) + ",\n");
    s.append(indent(depth + 1) + "exit: " + exit.debugString(depth + 1) + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
