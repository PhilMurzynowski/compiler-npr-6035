package edu.mit.compilers.ll;

import edu.mit.compilers.common.*;

import static edu.mit.compilers.common.Utilities.indent;

public class LLGenerator {

  private static String generateLabel(String label) {
    StringBuilder s = new StringBuilder();
    s.append(label);
    s.append(":\n");
    return s.toString();
  }

  private static String generateInstruction(String operator, String ...operands) {
    StringBuilder s = new StringBuilder();
    s.append(indent(1));
    s.append(operator);
    s.append(indent(1));
    if (operands.length > 0) {
      s.append(operands[0]);
      for (int i = 1; i < operands.length; ++i) {
        s.append(",");
        s.append(operands[i]);
      }
    }
    s.append("\n");
    return s.toString();
  }

  // DONE: Noah
  public static String generateProgram(LLProgram program) {
    StringBuilder s = new StringBuilder();

    // strings
    s.append("# string literal declarations");
    for (LLStringLiteralDeclaration stringLiteralDeclaration : program.getStringLiteralDeclarations()) {
      s.append(generateStringLiteralDeclaration(stringLiteralDeclaration));
    }
    s.append("\n");

    // imports
    s.append("# import declarations");
    for (LLImportDeclaration importDeclaration : program.getImportDeclarations()) {
      s.append(generateImportDeclaration(importDeclaration));
    }
    s.append("\n");

    // global scalars
    s.append("# global scalar fields");
    for (LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration : program.getScalarFieldDeclarations()) {
      s.append(generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration));
    }
    s.append("\n");

    // global arrays
    s.append("# global array fields");
    for (LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration : program.getArrayFieldDeclarations()) {
      s.append(generateGlobalArrayFieldDeclaration(globalArrayFieldDeclaration));
    }
    s.append("\n");

    // methods
    s.append("# methods");
    for (LLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      // add .globl for main method
      if (methodDeclaration.getIdentifier().equals("main")) {
        s.append(generateInstruction(".globl", "main"));
      }
      s.append(generateMethodDeclaration(methodDeclaration));
      s.append("\n");
    }

    return s.toString();
  }

  // DONE: Phil
  public static String generateDeclaration(LLDeclaration declaration) {
    if (declaration instanceof LLScalarFieldDeclaration scalarFieldDeclaration) {
      return LLGenerator.generateScalarFieldDeclaration(scalarFieldDeclaration);
    } else if (declaration instanceof LLArrayFieldDeclaration arrayFieldDeclaration) {
      return LLGenerator.generateArrayFieldDeclaration(arrayFieldDeclaration);
    } else if (declaration instanceof LLStringLiteralDeclaration stringLiteralDeclaration) {
      return LLGenerator.generateStringLiteralDeclaration(stringLiteralDeclaration);
    } else if (declaration instanceof LLMethodDeclaration methodDeclaration) {
      return LLGenerator.generateMethodDeclaration(methodDeclaration);
    } else if (declaration instanceof LLAliasDeclaration aliasDeclaration) {
      return LLGenerator.generateAliasDeclaration(aliasDeclaration);
    } else {
      throw new RuntimeException("not implemented");
    }
  }

  public static String generateImportDeclaration(LLImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static String generateScalarFieldDeclaration(LLScalarFieldDeclaration scalarFieldDeclaration) {
    if (scalarFieldDeclaration instanceof LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
      return LLGenerator.generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration);
    } else if (scalarFieldDeclaration instanceof LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
      return LLGenerator.generateLocalScalarFieldDeclaration(localScalarFieldDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static String generateArrayFieldDeclaration(LLArrayFieldDeclaration arrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Noah
  public static String generateGlobalScalarFieldDeclaration(LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    // <location()>:
    //   .quad 0

    StringBuilder s = new StringBuilder();

    s.append(generateLabel(globalScalarFieldDeclaration.location()));
    // TODO (nmp): should we have a different way of generating these?
    s.append(generateInstruction(".quad", "0"));

    return s.toString();
  }

  public static String generateGlobalArrayFieldDeclaration(LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateStringLiteralDeclaration(LLStringLiteralDeclaration stringLiteralDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static String generateBasicBlock(LLBasicBlock basicBlock) {
    StringBuilder s = new StringBuilder();

    if (!basicBlock.isGenerated()) {
      s.append(LLGenerator.generateLabel(basicBlock.location()));

      for (LLInstruction instruction : basicBlock.getInstructions()) {
        s.append(LLGenerator.generateInstruction(instruction));
      }

      basicBlock.setGenerated();

      if (basicBlock.hasFalseTarget()) {
        s.append(LLGenerator.generateInstruction("je", basicBlock.getFalseTarget().location()));
        s.append(LLGenerator.generateInstruction("jmp", basicBlock.getTrueTarget().location()));

        s.append(LLGenerator.generateBasicBlock(basicBlock.getTrueTarget()));
        s.append(LLGenerator.generateBasicBlock(basicBlock.getFalseTarget()));
      } else if (basicBlock.hasTrueTarget()) {
        s.append(LLGenerator.generateInstruction("jmp", basicBlock.getTrueTarget().location()));

        s.append(LLGenerator.generateBasicBlock(basicBlock.getTrueTarget()));
      }
    }

    return s.toString();
  }

  // DONE: Robert
  public static String generateControlFlowGraph(LLControlFlowGraph controlFlowGraph) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateBasicBlock(controlFlowGraph.getEntry()));

    if (!controlFlowGraph.getExit().isGenerated()) {
      throw new RuntimeException("failed to generate controlFlowGraph");
    }

    return s.toString();
  }

  // TODO: Phil
  public static String generateMethodDeclaration(LLMethodDeclaration methodDeclaration) {

    StringBuilder s = new StringBuilder();
    s.append(generateLabel(methodDeclaration.location()));

    // Prologue
    s.append(generateInstruction(
      "pushq",
      "%rbp"
    ));
    s.append(generateInstruction(
      "movq",
      "%rsp",
      "%rbp"
    ));
    s.append(generateInstruction(
      "subq",
      ""+methodDeclaration.setStackIndices(),
      "%rsp"
    ));

    // NOTE(phil): arguments handled by caller
    // NOTE(phil): declarations should already be handled by block hoisting
    /*
    for (LLLocalScalarFieldDeclarations scalarFieldDeclaration : methodDeclaration.getScalarFieldDeclarations()) {
    }
    for (LLLocalArrayFieldDeclaration arrayFieldDeclaration : methodDeclaration.getArrayFieldDeclarations()) {
    }
    // NOTE(phil): will allocate differently once using registers instead of temps
    for (LLAliasDeclaration aliasDeclaration : methodDeclaration.getAliasDeclarations()) {
    }
    */
  
    if (methodDeclaration.hasBody()) {
      LLControlFlowGraph body = methodDeclaration.getBody();
      s.append(generateControlFlowGraph(body));
    }

    // NOTE(phil): deal with potential lack of return statement here?
    //  Add one if method is of type void?
    //  Otherwise add runtime exception

    return s.toString();

  }

  public static String generateArgumentDeclaration(LLArgumentDeclaration argumentDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateLocalScalarFieldDeclaration(LLLocalScalarFieldDeclaration globalScalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateLocalArrayFieldDeclaration(LLLocalArrayFieldDeclaration globalArrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static String generateAliasDeclaration(LLAliasDeclaration aliasDeclaration) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("subq", "$8", "%rsp"));
    s.append(LLGenerator.generateInstruction("movq", "$0", aliasDeclaration.location()));

    return s.toString();
  }

  // DONE: Noah
  public static String generateInstruction(LLInstruction instruction) {
    if (instruction instanceof LLStoreScalar storeScalar) {
      return LLGenerator.generateStoreScalar(storeScalar);
    } else if (instruction instanceof LLStoreArray storeArray) {
      return LLGenerator.generateStoreArray(storeArray);
    } else if (instruction instanceof LLReturn llReturn) {
      return LLGenerator.generateReturn(llReturn);
    } else if (instruction instanceof LLBinary binary) {
      return LLGenerator.generateBinary(binary);
    } else if (instruction instanceof LLUnary unary) {
      return LLGenerator.generateUnary(unary);
    } else if (instruction instanceof LLLoadScalar loadScalar) {
      return LLGenerator.generateLoadScalar(loadScalar);
    } else if (instruction instanceof LLLoadArray loadArray) {
      return LLGenerator.generateLoadArray(loadArray);
    } else if (instruction instanceof LLInternalCall internalCall) {
      return LLGenerator.generateInternalCall(internalCall);
    } else if (instruction instanceof LLExternalCall externalCall) {
      return LLGenerator.generateExternalCall(externalCall);
    } else if (instruction instanceof LLLength length) {
      return LLGenerator.generateLength(length);
    } else if (instruction instanceof LLIntegerLiteral integerLiteral) {
      return LLGenerator.generateIntegerLiteral(integerLiteral);
    } else if (instruction instanceof LLStringLiteral stringLiteral) {
      return LLGenerator.generateStringLiteral(stringLiteral);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Phil
  public static String generateStoreScalar(LLStoreScalar storeScalar) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction(
      "movq",
      storeScalar.getExpression().location(),
      "%rax"
    )); 

    s.append(LLGenerator.generateInstruction(
      "movq",
      "%rax",
      storeScalar.getDeclaration().location()
    ));

    return s.toString();
  }

  public static String generateStoreArray(LLStoreArray storeArray) {
    throw new RuntimeException("not implemented");
  }

  public static String generateReturn(LLReturn ret) {
    throw new RuntimeException("not implemented");
    //movq %rbp, %rsp
    //popq %rbp
    //retq
  }

  // DONE: Robert
  public static String generateBinary(LLBinary binary) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));

    if (binary.getType().equals(BinaryExpressionType.ADD)) {
      s.append(LLGenerator.generateInstruction("addq", binary.getRight().location(), "%rax"));
    } else {
      throw new RuntimeException("not implemented");
    }

    s.append(LLGenerator.generateInstruction("movq", "%rax", binary.getResult().location()));

    return s.toString();
  }

  // DONE: Noah
  public static String generateUnary(LLUnary unary) {
    // movq <expression.location()>, %rax
    // <type> %rax
    // movq %rax, <result.location()>

    StringBuilder s = new StringBuilder();

    s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));

    if (unary.getType().equals(UnaryExpressionType.NEGATE)) {
      s.append(generateInstruction("negq", "%rax"));
    } else if (unary.getType().equals(UnaryExpressionType.NOT)) {
      s.append(generateInstruction("notq", "%rax"));
    }

    s.append(LLGenerator.generateInstruction("movq", "%rax", unary.getResult().location()));

    return s.toString();
  }

  // DONE: Phil
  public static String generateLoadScalar(LLLoadScalar loadScalar) {

    StringBuilder s = new StringBuilder();

    s.append(generateInstruction(
      "movq",
      loadScalar.getDeclaration().location(),
      "%rax"
    ));

    s.append(generateInstruction(
      "movq",
      "%rax",
      loadScalar.getResult().location()
    ));

    return s.toString();
  }

  public static String generateLoadArray(LLLoadArray loadArray) {
    throw new RuntimeException("not implemented");
  }

  public static String generateInternalCall(LLInternalCall internalCall) {
    throw new RuntimeException("not implemented");
  }

  public static String generateExternalCall(LLExternalCall externalCall) {
    throw new RuntimeException("not implemented");
  }

  public static String generateLength(LLLength length) {
    throw new RuntimeException("not implemented");
  }

  // DONE: Robert
  public static String generateIntegerLiteral(LLIntegerLiteral integerLiteral) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("movq", integerLiteral.getValue()+"", "%rax"));
    s.append(LLGenerator.generateInstruction("movq", "%rax", integerLiteral.getResult().location()));

    return s.toString();
  }

  public static String generateStringLiteral(LLStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
