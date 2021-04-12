package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import edu.mit.compilers.common.*;

import java.util.List;
import java.util.ArrayList;

public class DeadCodeBB {

  public static void apply(LLBasicBlock llBasicBlock, BitMap<LLDeclaration> entrybitMap, BitMap<LLDeclaration> exitBitMap) {

    List<LLInstruction> allLLInstructions = llBasicBlock.getInstructions();
    List<LLInstruction> aliveLLInstructions = new ArrayList<LLInstruction>(); 

    for (int i = allLLInstructions.size() - 1; i >= 0; i--) {
      
    } 
    
  }

}
