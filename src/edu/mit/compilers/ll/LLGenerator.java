package edu.mit.compilers.ll;

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
    return s.toString();
  }

  // TODO: Noah
  public static String generateProgram(LLProgram program) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Phil
  public static String generateDeclaration(LLDeclaration declaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateImportDeclaration(LLImportDeclaration importDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Robert
  public static String generateScalarFieldDeclaration(LLScalarFieldDeclaration scalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  public static String generateArrayFieldDeclaration(LLArrayFieldDeclaration arrayFieldDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static String generateGlobalScalarFieldDeclaration(LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    throw new RuntimeException("not implemented");
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

  // TODO: Robert
  public static String generateAliasDeclaration(LLAliasDeclaration aliasDeclaration) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static String generateInstruction(LLInstruction instruction) {
    throw new RuntimeException("not implemented");
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

  // TODO: Robert
  public static String generateBinary(LLBinary binary) {
    throw new RuntimeException("not implemented");
  }

  // TODO: Noah
  public static String generateUnary(LLUnary unary) {
    throw new RuntimeException("not implemented");
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

  // TODO: Robert
  public static String generateIntegerLiteral(LLIntegerLiteral integerLiteral) {
    throw new RuntimeException("not implemented");
  }

  public static String generateStringLiteral(LLStringLiteral stringLiteral) {
    throw new RuntimeException("not implemented");
  }

}
