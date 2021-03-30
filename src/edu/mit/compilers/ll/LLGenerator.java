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

  // TODO: Phil
  public static String generateDeclaration(LLDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateImportDeclaration(LLImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateScalarFieldDeclaration(LLScalarFieldDeclaration scalarFieldDeclaration) {
    if (scalarFieldDeclaration instanceof LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
      return LLGenerator.generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration);
    } else if (scalarFieldDeclaration instanceof LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
      return LLGenerator.generateLocalScalarFieldDeclaration(localScalarFieldDeclaration);
    } else {
      throw new RuntimeException("not implemented");
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

    s.append(generateLabel(globalScalarFieldDeclaration.getIdentifier()));
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

  // TODO: Phil
  public static String generateMethodDeclaration(LLMethodDeclaration methodDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Phil
  public static String generateStoreScalar(LLStoreScalar storeScalar) {
    throw new RuntimeException("not implemented");
  }

  public static String generateStoreArray(LLStoreArray storeArray) {
    throw new RuntimeException("not implemented");
  }

  public static String generateReturn(LLReturn ret) {
    throw new RuntimeException("not implemented");
  }

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

  // TODO: Phil
  public static String generateLoadScalar(LLLoadScalar loadScalar) {
    throw new RuntimeException("not implemented");
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
