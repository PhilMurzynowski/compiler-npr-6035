package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

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

  public static String generateProgram(LLProgram program) {
    StringBuilder s = new StringBuilder();

    s.append(".data\n\n");

    // strings
    s.append("# string literal declarations\n");
    for (LLStringLiteralDeclaration stringLiteralDeclaration : program.getStringLiteralDeclarations()) {
      s.append(generateStringLiteralDeclaration(stringLiteralDeclaration));
    }
    s.append("\n");

    // imports
    s.append("# import declarations\n");
    for (LLImportDeclaration importDeclaration : program.getImportDeclarations()) {
      s.append(generateImportDeclaration(importDeclaration));
    }
    s.append("\n");

    // global scalars
    s.append("# global scalar fields\n");
    for (LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration : program.getScalarFieldDeclarations()) {
      s.append(generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration));
    }
    s.append("\n");

    // global arrays
    s.append("# global array fields\n");
    for (LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration : program.getArrayFieldDeclarations()) {
      s.append(generateGlobalArrayFieldDeclaration(globalArrayFieldDeclaration));
    }
    s.append("\n");

    s.append(".text\n\n");

    // methods
    s.append("# methods\n");
    for (LLMethodDeclaration methodDeclaration : program.getMethodDeclarations()) {
      // add .globl for main method
      if (methodDeclaration.getIdentifier().equals("main")) {
        s.append(".globl main\n");
      }
      s.append(generateMethodDeclaration(methodDeclaration));
      s.append("\n");
    }

    return s.toString();
  }

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

  // DONE: Noah
  public static String generateImportDeclaration(LLImportDeclaration importDeclaration) {
    // gcc takes care of this for us, so just leaving comments
    final StringBuilder s = new StringBuilder();
    s.append(indent(1)).append("# imported ").append(importDeclaration.getIdentifier()).append("\n");
    return s.toString();
  }

  public static String generateScalarFieldDeclaration(LLScalarFieldDeclaration scalarFieldDeclaration) {
    if (scalarFieldDeclaration instanceof LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
      return LLGenerator.generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration);
    } else if (scalarFieldDeclaration instanceof LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
      return LLGenerator.generateLocalScalarFieldDeclaration(localScalarFieldDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // DONE: Phil
  public static String generateArrayFieldDeclaration(LLArrayFieldDeclaration arrayFieldDeclaration) {
    if (arrayFieldDeclaration instanceof LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
      return LLGenerator.generateGlobalArrayFieldDeclaration(globalArrayFieldDeclaration);
    } else if (arrayFieldDeclaration instanceof LLLocalArrayFieldDeclaration localArrayFieldDeclaration) {
      return LLGenerator.generateLocalArrayFieldDeclaration(localArrayFieldDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  public static String generateGlobalScalarFieldDeclaration(LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    // <location()>:
    //   .quad 0

    StringBuilder s = new StringBuilder();

    s.append(generateLabel(globalScalarFieldDeclaration.location()));
    s.append(generateInstruction(".quad", "0"));

    return s.toString();
  }

  // DONE: Robert
  public static String generateGlobalArrayFieldDeclaration(LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    StringBuilder s = new StringBuilder();

    s.append(generateLabel(globalArrayFieldDeclaration.location()));
    s.append(generateInstruction(".zero", (globalArrayFieldDeclaration.getLength() * 8)+""));

    return s.toString();
  }

  // DONE: Noah
  public static String generateStringLiteralDeclaration(LLStringLiteralDeclaration stringLiteralDeclaration) {
    StringBuilder s = new StringBuilder();

    s.append(generateLabel(stringLiteralDeclaration.location()));
    s.append(generateInstruction(".string \"" + stringLiteralDeclaration.getValue() + "\""));
    s.append(generateInstruction(".align", "16"));

    return s.toString();
  }

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

  public static String generateControlFlowGraph(LLControlFlowGraph controlFlowGraph) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateBasicBlock(controlFlowGraph.getEntry()));

    if (!controlFlowGraph.getExit().isGenerated()) {
      throw new RuntimeException("failed to generate controlFlowGraph");
    }

    return s.toString();
  }

  // DONE: Phil
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

    int stackSize = methodDeclaration.setStackIndices();
    if (stackSize > 0) {
      if (stackSize % 16 != 0) {
        stackSize += 8;
      }
      s.append(generateInstruction(
        "subq",
        "$"+stackSize,
        "%rsp"
      ));
    }

    // NOTE(phil): arguments handled by caller
    // NOTE(phil): declarations should already be handled by block hoisting
  
    if (methodDeclaration.hasBody()) {
      LLControlFlowGraph body = methodDeclaration.getBody();
      s.append(generateControlFlowGraph(body));
    }

    return s.toString();
  }

  // DONE: Robert (these could probably be removed...)
  public static String generateArgumentDeclaration(LLArgumentDeclaration argumentDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // DONE: Noah
  public static String generateLocalScalarFieldDeclaration(LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // DONE: Phil
  public static String generateLocalArrayFieldDeclaration(LLLocalArrayFieldDeclaration localArrayFieldDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // NOTE(phil): this may not be necessary due to hoisting, and will always overwrite alias before reading
  public static String generateAliasDeclaration(LLAliasDeclaration aliasDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

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
    } else if (instruction instanceof LLCompare compare) {
      return LLGenerator.generateCompare(compare);
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

  // DONE: Robert
  public static String generateStoreArray(LLStoreArray storeArray) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("movq", storeArray.getIndex().location(), "%r10"));
    s.append(LLGenerator.generateInstruction("movq", storeArray.getExpression().location(), "%rax"));
    s.append(LLGenerator.generateInstruction("movq", "%rax", storeArray.getDeclaration().index("%r10")));

    return s.toString();
  }

  public static String generateReturn(LLReturn ret) {
    StringBuilder s = new StringBuilder();

    Optional<LLDeclaration> returnExpression = ret.getExpression();
    if (returnExpression.isPresent()) {
      s.append(LLGenerator.generateInstruction("movq", returnExpression.get().location(), "%rax"));
    } else {
      s.append(LLGenerator.generateInstruction("movq", "$0", "%rax"));
    }
    
    s.append(LLGenerator.generateInstruction("movq", "%rbp", "%rsp"));
    s.append(LLGenerator.generateInstruction("popq", "%rbp"));
    s.append(LLGenerator.generateInstruction("retq"));

    return s.toString();
  }

  // DONE: Robert
  public static String generateBinary(LLBinary binary) {
    StringBuilder s = new StringBuilder();

    if (binary.getType().equals(BinaryExpressionType.OR)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("orq", binary.getRight().location(), "%rax"));
    } else if (binary.getType().equals(BinaryExpressionType.AND)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("andq", binary.getRight().location(), "%rax"));
    } else if (binary.getType().equals(BinaryExpressionType.EQUAL)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("sete", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.NOT_EQUAL)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("setne", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.LESS_THAN)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("setl", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.LESS_THAN_OR_EQUAL)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("setle", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.GREATER_THAN)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("setg", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.GREATER_THAN_OR_EQUAL)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("xorq", "%rax", "%rax"));
      s.append(LLGenerator.generateInstruction("cmpq", binary.getRight().location(), "%r10"));
      s.append(LLGenerator.generateInstruction("setge", "%al"));
    } else if (binary.getType().equals(BinaryExpressionType.ADD)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("addq", binary.getRight().location(), "%rax"));
    } else if (binary.getType().equals(BinaryExpressionType.SUBTRACT)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("subq", binary.getRight().location(), "%rax"));
    } else if (binary.getType().equals(BinaryExpressionType.MULTIPLY)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("imulq", binary.getRight().location(), "%rax"));
    } else if (binary.getType().equals(BinaryExpressionType.DIVIDE)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("cqto"));
      s.append(LLGenerator.generateInstruction("idivq", binary.getRight().location()));
    } else if (binary.getType().equals(BinaryExpressionType.MODULUS)) {
      s.append(LLGenerator.generateInstruction("movq", binary.getLeft().location(), "%rax"));
      s.append(LLGenerator.generateInstruction("cqto"));
      s.append(LLGenerator.generateInstruction("idivq", binary.getRight().location()));
      s.append(LLGenerator.generateInstruction("movq", "%rdx", "%rax"));
    } else {
      throw new RuntimeException("not implemented");
    }

    s.append(LLGenerator.generateInstruction("movq", "%rax", binary.getResult().location()));

    return s.toString();
  }

  public static String generateUnary(LLUnary unary) {
    // movq <expression.location()>, %rax
    // <type> %rax
    // movq %rax, <result.location()>

    StringBuilder s = new StringBuilder();


    if (unary.getType().equals(UnaryExpressionType.NEGATE)) {
      s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));
      s.append(generateInstruction("negq", "%rax"));
      s.append(LLGenerator.generateInstruction("movq", "%rax", unary.getResult().location()));
    } else if (unary.getType().equals(UnaryExpressionType.NOT)) {
      s.append(generateInstruction("movq", unary.getExpression().location(), "%r10"));
      s.append(generateInstruction("xorq", "%rax", "%rax"));
      s.append(generateInstruction("testq", "%r10", "%r10"));
      s.append(generateInstruction("sete", "%al"));
      s.append(generateInstruction("movq", "%rax", unary.getResult().location()));
    } else if (unary.getType().equals(UnaryExpressionType.INCREMENT)) {
      s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));
      s.append(generateInstruction("incq", "%rax"));
      s.append(LLGenerator.generateInstruction("movq", "%rax", unary.getResult().location()));
    } else if (unary.getType().equals(UnaryExpressionType.DECREMENT)) {
      s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));
      s.append(generateInstruction("decq", "%rax"));
      s.append(LLGenerator.generateInstruction("movq", "%rax", unary.getResult().location()));
    }


    return s.toString();
  }

  // DONE: Robert
  public static String generateCompare(LLCompare compare) {
    StringBuilder s = new StringBuilder();

    s.append(generateInstruction("movq", compare.getLeft().location(), "%rax"));
    s.append(generateInstruction("cmpq", compare.getRight().location(), "%rax"));

    return s.toString();
  }

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

  // DONE: Noah
  public static String generateLoadArray(LLLoadArray loadArray) {
    StringBuilder s = new StringBuilder();

    s.append(generateInstruction("movq", loadArray.getIndex().location(), "%r10"));
    s.append(generateInstruction("movq", loadArray.getLocation().index("%r10"), "%rax"));
    s.append(generateInstruction("movq", "%rax", loadArray.getResult().location()));

    return s.toString();
  }

  // DONE: Phil (same as external for now)
  // NOTE(phil): add caller saved addresses
  public static String generateInternalCall(LLInternalCall internalCall) {
    StringBuilder s = new StringBuilder();

    List<String> registers = List.of("%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9");
    List<LLDeclaration> arguments = internalCall.getArguments();

    for (String register : registers) {
      s.append(generateInstruction("pushq", register));
    }

    for (int i = 0; i < registers.size() && i < arguments.size(); ++i) {
      s.append(generateInstruction("movq", arguments.get(i).location(), registers.get(i)));
    }

    if (arguments.size() > registers.size() && (arguments.size() - registers.size()) % 2 != 0) {
      s.append(generateInstruction("subq", "$8", "%rsp"));
    }

    for (int i = arguments.size() - 1; i > 5; --i) {
      s.append(generateInstruction("pushq", arguments.get(i).location()));
    }

    s.append(generateInstruction("callq", internalCall.getDeclaration().location()));

    if (arguments.size() > registers.size()) {
      int size = (arguments.size() - registers.size()) * 8;
      if (size % 16 != 0) {
        size += 8;
      }
      s.append(generateInstruction("addq", "$"+size, "%rsp"));
    }

    s.append(generateInstruction("movq", "%rax", internalCall.getResult().location()));

    for (int i = registers.size() - 1; i >= 0; --i) {
      s.append(generateInstruction("popq", registers.get(i)));
    }

    return s.toString();
  }

  // DONE: Robert
  // NOTE(phil): 16 byte align?
  public static String generateExternalCall(LLExternalCall externalCall) {
    StringBuilder s = new StringBuilder();

    List<String> registers = List.of("%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9");
    List<LLDeclaration> arguments = externalCall.getArguments();

    for (String register : registers) {
      s.append(generateInstruction("pushq", register));
    }

    for (int i = 0; i < registers.size() && i < arguments.size(); ++i) {
      s.append(generateInstruction("movq", arguments.get(i).location(), registers.get(i)));
    }

    if (arguments.size() > registers.size() && (arguments.size() - registers.size()) % 2 != 0) {
      s.append(generateInstruction("subq", "$8", "%rsp"));
    }

    for (int i = arguments.size() - 1; i > 5; --i) {
      s.append(generateInstruction("pushq", arguments.get(i).location()));
    }

    s.append(generateInstruction("callq", externalCall.getDeclaration().location()));

    if (arguments.size() > registers.size()) {
      int size = (arguments.size() - registers.size()) * 8;
      if (size % 16 != 0) {
        size += 8;
      }
      s.append(generateInstruction("addq", "$"+size, "%rsp"));
    }

    s.append(generateInstruction("movq", "%rax", externalCall.getResult().location()));

    for (int i = registers.size() - 1; i >= 0; --i) {
      s.append(generateInstruction("popq", registers.get(i)));
    }

    return s.toString();
  }

  // DONE: Noah
  public static String generateLength(LLLength length) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("movq", "$"+length.getDeclaration().getLength(), "%rax"));
    s.append(LLGenerator.generateInstruction("movq", "%rax", length.getResult().location()));

    return s.toString();
  }

  public static String generateIntegerLiteral(LLIntegerLiteral integerLiteral) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("movq", "$"+integerLiteral.getValue(), "%rax"));
    s.append(LLGenerator.generateInstruction("movq", "%rax", integerLiteral.getResult().location()));

    return s.toString();
  }

  // DONE: Phil
  public static String generateStringLiteral(LLStringLiteral stringLiteral) {
    StringBuilder s = new StringBuilder();

    s.append(LLGenerator.generateInstruction("leaq", stringLiteral.getDeclaration().location()+"(%rip)", "%rax"));
    s.append(LLGenerator.generateInstruction("movq", "%rax", stringLiteral.getResult().location()));

    return s.toString();
  }

}
