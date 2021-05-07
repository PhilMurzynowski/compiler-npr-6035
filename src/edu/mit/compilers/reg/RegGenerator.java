package edu.mit.compilers.reg;

import java.util.Optional;
import java.util.List;

import edu.mit.compilers.common.*;
import edu.mit.compilers.ll.*;

import static edu.mit.compilers.common.Utilities.indent;
import static edu.mit.compilers.reg.Registers.q2b;

public class RegGenerator {

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

  // no work needed
  public static String generateProgram(LLProgram program) {
    StringBuilder s = new StringBuilder();

    s.append(".data\n\n");

    // strings
    s.append("# string literal declarations\n");
    for (LLStringLiteralDeclaration stringLiteralDeclaration : program.getStringLiteralDeclarations()) {
      s.append(generateStringLiteralDeclaration(stringLiteralDeclaration));
    }

    s.append(generateLabel("no_return_value"));
    s.append(generateInstruction(".string \"Reached end of non-void method without returning a value.\\n\""));
    s.append(generateInstruction(".align", "16"));

    s.append(generateLabel("out_of_bounds"));
    s.append(generateInstruction(".string \"Array index access is out-of-bounds.\\n\""));
    s.append(generateInstruction(".align", "16"));

    s.append(generateLabel("divide_by_zero"));
    s.append(generateInstruction(".string \"Attempted to divide by zero.\\n\""));
    s.append(generateInstruction(".align", "16"));

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

  // no work needed
  public static String generateDeclaration(LLDeclaration declaration) {
    if (declaration instanceof LLScalarFieldDeclaration scalarFieldDeclaration) {
      return generateScalarFieldDeclaration(scalarFieldDeclaration);
    } else if (declaration instanceof LLArrayFieldDeclaration arrayFieldDeclaration) {
      return generateArrayFieldDeclaration(arrayFieldDeclaration);
    } else if (declaration instanceof LLStringLiteralDeclaration stringLiteralDeclaration) {
      return generateStringLiteralDeclaration(stringLiteralDeclaration);
    } else if (declaration instanceof LLMethodDeclaration methodDeclaration) {
      return generateMethodDeclaration(methodDeclaration);
    } else if (declaration instanceof LLAliasDeclaration aliasDeclaration) {
      return generateAliasDeclaration(aliasDeclaration);
    } else {
      throw new RuntimeException("not implemented");
    }
  }

  // no work needed
  public static String generateImportDeclaration(LLImportDeclaration importDeclaration) {
    // gcc takes care of this for us, so just leaving comments
    final StringBuilder s = new StringBuilder();
    s.append(indent(1)).append("# imported ").append(importDeclaration.getIdentifier()).append("\n");
    return s.toString();
  }

  // no work needed
  public static String generateScalarFieldDeclaration(LLScalarFieldDeclaration scalarFieldDeclaration) {
    if (scalarFieldDeclaration instanceof LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
      return generateGlobalScalarFieldDeclaration(globalScalarFieldDeclaration);
    } else if (scalarFieldDeclaration instanceof LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
      return generateLocalScalarFieldDeclaration(localScalarFieldDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // no work needed
  public static String generateArrayFieldDeclaration(LLArrayFieldDeclaration arrayFieldDeclaration) {
    if (arrayFieldDeclaration instanceof LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
      return generateGlobalArrayFieldDeclaration(globalArrayFieldDeclaration);
    } else if (arrayFieldDeclaration instanceof LLLocalArrayFieldDeclaration localArrayFieldDeclaration) {
      return generateLocalArrayFieldDeclaration(localArrayFieldDeclaration);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // no work needed
  public static String generateGlobalScalarFieldDeclaration(LLGlobalScalarFieldDeclaration globalScalarFieldDeclaration) {
    // <location()>:
    //   .quad 0

    StringBuilder s = new StringBuilder();

    s.append(generateLabel(globalScalarFieldDeclaration.location()));
    s.append(generateInstruction(".quad", "0"));

    return s.toString();
  }

  // no work needed
  public static String generateGlobalArrayFieldDeclaration(LLGlobalArrayFieldDeclaration globalArrayFieldDeclaration) {
    StringBuilder s = new StringBuilder();

    s.append(generateLabel(globalArrayFieldDeclaration.location()));
    s.append(generateInstruction(".zero", (globalArrayFieldDeclaration.getLength() * 8)+""));

    return s.toString();
  }

  // no work needed
  public static String generateStringLiteralDeclaration(LLStringLiteralDeclaration stringLiteralDeclaration) {
    StringBuilder s = new StringBuilder();

    s.append(generateLabel(stringLiteralDeclaration.location()));
    s.append(generateInstruction(".string \"" + stringLiteralDeclaration.getValue() + "\""));
    s.append(generateInstruction(".align", "16"));

    return s.toString();
  }

  // no work needed
  public static String generateBasicBlock(LLBasicBlock basicBlock) {
    StringBuilder s = new StringBuilder();

    if (!basicBlock.isGenerated()) {
      s.append(generateLabel(basicBlock.location()));

      for (LLInstruction instruction : basicBlock.getInstructions()) {
        s.append(generateInstruction(instruction));
      }

      basicBlock.setGenerated();

      if (basicBlock.hasFalseTarget()) {
        final List<LLInstruction> instructions = basicBlock.getInstructions();

        if (instructions.size() < 1) {
          throw new RuntimeException("should have at least one instruction (a comparison)");
        }

        if (instructions.get(instructions.size() - 1) instanceof LLCompare compareInstruction) {
          final ComparisonType comparisonType = compareInstruction.getType();

          if (comparisonType.equals(ComparisonType.EQUAL)) {
            s.append(generateInstruction("jne", basicBlock.getFalseTarget().location()));
          } else if (comparisonType.equals(ComparisonType.NOT_EQUAL)) {
            s.append(generateInstruction("je", basicBlock.getFalseTarget().location()));
          } else if (comparisonType.equals(ComparisonType.LESS_THAN)) {
            s.append(generateInstruction("jge", basicBlock.getFalseTarget().location()));
          } else if (comparisonType.equals(ComparisonType.LESS_THAN_OR_EQUAL)) {
            s.append(generateInstruction("jg", basicBlock.getFalseTarget().location()));
          } else if (comparisonType.equals(ComparisonType.GREATER_THAN)) {
            s.append(generateInstruction("jle", basicBlock.getFalseTarget().location()));
          } else if (comparisonType.equals(ComparisonType.GREATER_THAN_OR_EQUAL)) {
            s.append(generateInstruction("jl", basicBlock.getFalseTarget().location()));
          } else {
            throw new RuntimeException("unreachable");
          }

          if (basicBlock.getTrueTarget().isGenerated()) {
            s.append(generateInstruction("jmp", basicBlock.getTrueTarget().location()));
          } else {
            s.append(generateBasicBlock(basicBlock.getTrueTarget()));
          }

          s.append(generateBasicBlock(basicBlock.getFalseTarget()));
        } else {
          throw new RuntimeException("expected last instruction to be a comparison");
        }
      } else if (basicBlock.hasTrueTarget()) {
        if (basicBlock.getTrueTarget().isGenerated()) {
          s.append(generateInstruction("jmp", basicBlock.getTrueTarget().location()));
        } else {
          s.append(generateBasicBlock(basicBlock.getTrueTarget()));
        }
      }
    }

    return s.toString();
  }

  public static String generateControlFlowGraph(LLControlFlowGraph controlFlowGraph) {
    StringBuilder s = new StringBuilder();

    s.append(generateBasicBlock(controlFlowGraph.getEntry()));

    if (controlFlowGraph.hasExit()) {
      assert controlFlowGraph.expectExit().isGenerated() : "failed to generate control flow graph exit";
    }

    for (LLBasicBlock exception : controlFlowGraph.getExceptions()) {
      assert exception.isGenerated() : "failed to generate control flow graph exception";
    }

    return s.toString();
  }

  // Noah: DONE
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

    // callee saved registers
    for (String register : Registers.CALLEE_SAVED) {
      if (!register.equals(Registers.RBP)) {
        s.append(generateInstruction("pushq", register));
      }
    }

    int stackSize = methodDeclaration.setStackIndices();
    stackSize += 8; // callq pushes rax
    if (stackSize > 0) {
      if (stackSize % 16 == 0) {
        stackSize += 8;
      }
      s.append(generateInstruction(
          "subq",
          "$"+stackSize,
          "%rsp"
      ));
    }

    final List<String> registers = List.of("%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9");

    for (int i = 0; i < registers.size() && i < methodDeclaration.getArgumentDeclarations().size(); ++i) {
      s.append(generateInstruction("movq", registers.get(i), methodDeclaration.getArgumentDeclarations().get(i).location()));
    }

    // NOTE(phil): arguments handled by caller
    // NOTE(phil): declarations should already be handled by block hoisting

    if (methodDeclaration.hasBody()) {
      LLControlFlowGraph body = methodDeclaration.getBody();
      s.append(generateControlFlowGraph(body));
    }

    return s.toString();
  }

  // no work needed
  public static String generateArgumentDeclaration(LLArgumentDeclaration argumentDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // no work needed
  public static String generateLocalScalarFieldDeclaration(LLLocalScalarFieldDeclaration localScalarFieldDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // no work needed
  public static String generateLocalArrayFieldDeclaration(LLLocalArrayFieldDeclaration localArrayFieldDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // no work needed
  public static String generateAliasDeclaration(LLAliasDeclaration aliasDeclaration) {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  // no work needed
  public static String generateInstruction(LLInstruction instruction) {
    if (instruction instanceof LLStoreScalar storeScalar) {
      return generateStoreScalar(storeScalar);
    } else if (instruction instanceof LLStoreArray storeArray) {
      return generateStoreArray(storeArray);
    } else if (instruction instanceof LLReturn llReturn) {
      return generateReturn(llReturn);
    } else if (instruction instanceof LLException llException) {
      return generateException(llException);
    } else if (instruction instanceof LLBinary binary) {
      return generateBinary(binary);
    } else if (instruction instanceof LLUnary unary) {
      return generateUnary(unary);
    } else if (instruction instanceof LLCompare compare) {
      return generateCompare(compare);
    } else if (instruction instanceof LLLoadScalar loadScalar) {
      return generateLoadScalar(loadScalar);
    } else if (instruction instanceof LLLoadArray loadArray) {
      return generateLoadArray(loadArray);
    } else if (instruction instanceof LLInternalCall internalCall) {
      return generateInternalCall(internalCall);
    } else if (instruction instanceof LLExternalCall externalCall) {
      return generateExternalCall(externalCall);
    } else if (instruction instanceof LLLength length) {
      return generateLength(length);
    } else if (instruction instanceof LLIntegerLiteral integerLiteral) {
      return generateIntegerLiteral(integerLiteral);
    } else if (instruction instanceof LLStringLiteral stringLiteral) {
      return generateStringLiteral(stringLiteral);
    } else if (instruction instanceof LLCopy copy) {
      return generateCopy(copy);
    } else {
      throw new RuntimeException("unreachable");
    }
  }

  // Phil: DONE
  public static String generateStoreScalar(LLStoreScalar storeScalar) {
    StringBuilder s = new StringBuilder();

    final LLDeclaration expression = storeScalar.getExpression();
    final LLDeclaration declaration = storeScalar.getDeclaration();

    final boolean expressionInRegister = storeScalar.useInRegister(expression);
    final boolean declarationInRegister = storeScalar.defInRegister();

    final String declarationLocation = storeScalar.getDefWebLocation();
    final String expressionLocation = storeScalar.getUseWebLocation(expression);

    if (expressionInRegister || declarationInRegister) { 
      s.append(generateInstruction("movq", expressionLocation, declarationLocation));
    } else {
      s.append(generateInstruction("movq", expressionLocation, "%rax"));
      s.append(generateInstruction("movq", "%rax", declarationLocation));
    }

    return s.toString();
  }

  // Robert: DONE
  public static String generateStoreArray(LLStoreArray storeArray) {
    final StringBuilder s = new StringBuilder();

    final LLDeclaration index = storeArray.getIndex();
    final LLDeclaration expression = storeArray.getExpression();

    final boolean indexInRegister = storeArray.useInRegister(index);
    final boolean expressionInRegister = storeArray.useInRegister(expression);

    final String indexLocation = storeArray.getUseWebLocation(index);
    final String expressionLocation = storeArray.getUseWebLocation(expression);

    if (indexInRegister && expressionInRegister) {
      s.append(generateInstruction("movq", expressionLocation, storeArray.getDeclaration().index(indexLocation)));
    } else if (indexInRegister && !expressionInRegister) {
      s.append(generateInstruction("movq", expressionLocation, "%rax"));
      s.append(generateInstruction("movq", "%rax", storeArray.getDeclaration().index(indexLocation)));
    } else if (!indexInRegister && expressionInRegister) {
      s.append(generateInstruction("movq", indexLocation, "%r10"));
      s.append(generateInstruction("movq", expressionLocation, storeArray.getDeclaration().index("%r10")));
    } else {
      s.append(generateInstruction("movq", indexLocation, "%r10"));
      s.append(generateInstruction("movq", expressionLocation, "%rax"));
      s.append(generateInstruction("movq", "%rax", storeArray.getDeclaration().index("%r10")));
    }

    return s.toString();
  }

  // Noah: DONE
  public static String generateReturn(LLReturn ret) {
    StringBuilder s = new StringBuilder();

    Optional<LLDeclaration> returnExpression = ret.getExpression();
    if (returnExpression.isPresent()) {
      s.append(generateInstruction("movq", ret.getUseWebLocation(returnExpression.get()), "%rax"));
    } else {
      s.append(generateInstruction("movq", "$0", "%rax"));
    }

    for (int i = Registers.CALLEE_SAVED.size() - 1; i >= 0; --i) {
      if (!Registers.CALLEE_SAVED.get(i).equals(Registers.RBP)) {
        s.append(generateInstruction("popq", Registers.CALLEE_SAVED.get(i)));
      }
    }

    s.append(generateInstruction("movq", "%rbp", "%rsp"));
    s.append(generateInstruction("popq", "%rbp"));

    s.append(generateInstruction("retq"));

    return s.toString();
  }

  // Phil: DONE
  public static String generateException(LLException exception) {
    StringBuilder s = new StringBuilder();

    if (exception.getType().equals(LLException.Type.OutOfBounds)) {
      s.append(generateInstruction("leaq", "out_of_bounds(%rip)", "%rdi"));
      s.append(generateInstruction("callq", "printf"));
      s.append(generateInstruction("movq", "$-1", "%rdi"));
      s.append(generateInstruction("callq", "exit"));
    } else if (exception.getType().equals(LLException.Type.NoReturnValue)) {
      s.append(generateInstruction("leaq", "no_return_value(%rip)", "%rdi"));
      s.append(generateInstruction("callq", "printf"));
      s.append(generateInstruction("movq", "$-2", "%rdi"));
      s.append(generateInstruction("callq", "exit"));
    } else if (exception.getType().equals(LLException.Type.DivideByZero)) {
      s.append(generateInstruction("leaq", "divide_by_zero(%rip)", "%rdi"));
      s.append(generateInstruction("callq", "printf"));
      s.append(generateInstruction("movq", "$-3", "%rdi"));
      s.append(generateInstruction("callq", "exit"));
    } else {
      throw new RuntimeException("unreachable");
    }

    return s.toString();
  }

  // Robert: TODO
  public static String generateBinary(LLBinary binary) {
    final StringBuilder s = new StringBuilder();

    final LLDeclaration left = binary.getLeft();
    final LLDeclaration right = binary.getRight();

    final boolean resultInRegister = binary.defInRegister();
    final boolean leftInRegister = binary.useInRegister(left);
    final boolean rightInRegister = binary.useInRegister(right);

    final String resultLocation = binary.getDefWebLocation();
    final String leftLocation = binary.getUseWebLocation(left);
    final String rightLocation = binary.getUseWebLocation(right);

    switch (binary.getType()) {
      case OR:
        if (resultInRegister) {
          if (leftInRegister && leftLocation.equals(resultLocation)) {
            s.append(generateInstruction("orq", rightLocation, resultLocation));
          } else if (rightInRegister && rightLocation.equals(resultLocation)) {
            s.append(generateInstruction("orq", leftLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("orq", rightLocation, resultLocation));
          }
        } else {
          s.append(generateInstruction("movq", leftLocation, "%rax"));
          s.append(generateInstruction("orq", rightLocation, "%rax"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case AND:
        if (resultInRegister) {
          if (leftInRegister && leftLocation.equals(resultLocation)) {
            s.append(generateInstruction("andq", rightLocation, resultLocation));
          } else if (rightInRegister && rightLocation.equals(resultLocation)) {
            s.append(generateInstruction("andq", leftLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("andq", rightLocation, resultLocation));
          }
        } else {
          s.append(generateInstruction("movq", leftLocation, "%rax"));
          s.append(generateInstruction("andq", rightLocation, "%rax"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case EQUAL:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if(resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
            }
          }
          s.append(generateInstruction("sete", q2b(resultLocation)));
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("sete", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("sete", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case NOT_EQUAL:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if(resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
            }
          }
          s.append(generateInstruction("setne", q2b(resultLocation)));
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setne", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setne", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case LESS_THAN:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if(resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
            s.append(generateInstruction("setl", q2b(resultLocation)));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
              s.append(generateInstruction("setge", q2b(resultLocation)));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
              s.append(generateInstruction("setl", q2b(resultLocation)));
            }
          }
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setl", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setl", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case LESS_THAN_OR_EQUAL:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if (resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
            s.append(generateInstruction("setle", q2b(resultLocation)));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
              s.append(generateInstruction("setg", q2b(resultLocation)));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
              s.append(generateInstruction("setle", q2b(resultLocation)));
            }
          }
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setle", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setle", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case GREATER_THAN:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if (resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
            s.append(generateInstruction("setg", q2b(resultLocation)));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
              s.append(generateInstruction("setle", q2b(resultLocation)));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
              s.append(generateInstruction("setg", q2b(resultLocation)));
            }
          }
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setg", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setg", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case GREATER_THAN_OR_EQUAL:
        if (resultInRegister && (leftInRegister || rightInRegister)) {
          if (resultLocation.equals(leftLocation) || resultLocation.equals(rightLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%r10"));
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            s.append(generateInstruction("cmpq", rightLocation, "%r10"));
            s.append(generateInstruction("setge", q2b(resultLocation)));
          } else {
            s.append(generateInstruction("xorq", resultLocation, resultLocation));
            if (left instanceof LLConstantDeclaration) {
              s.append(generateInstruction("cmpq", leftLocation, rightLocation));
              s.append(generateInstruction("setl", q2b(resultLocation)));
            } else {
              s.append(generateInstruction("cmpq", rightLocation, leftLocation));
              s.append(generateInstruction("setge", q2b(resultLocation)));
            }
          }
        } else if (resultInRegister && !leftInRegister && !rightInRegister) {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", resultLocation, resultLocation));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setge", q2b(resultLocation)));
        } else {
          s.append(generateInstruction("movq", leftLocation, "%r10"));
          s.append(generateInstruction("xorq", "%rax", "%rax"));
          s.append(generateInstruction("cmpq", rightLocation, "%r10"));
          s.append(generateInstruction("setge", "%al"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case ADD:
        if (resultInRegister) {
          if (leftInRegister && leftLocation.equals(resultLocation)) {
            s.append(generateInstruction("addq", rightLocation, resultLocation));
          } else if (rightInRegister && rightLocation.equals(resultLocation)) {
            s.append(generateInstruction("addq", leftLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("addq", rightLocation, resultLocation));
          }
        } else {
          s.append(generateInstruction("movq", leftLocation, "%rax"));
          s.append(generateInstruction("addq", rightLocation, "%rax"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case SUBTRACT:
        if (resultInRegister) {
          if (leftInRegister && leftLocation.equals(resultLocation)) {
            s.append(generateInstruction("subq", rightLocation, resultLocation));
          } else if (rightInRegister && rightLocation.equals(resultLocation)) {
            s.append(generateInstruction("movq", leftLocation, "%rax"));
            s.append(generateInstruction("subq", rightLocation, "%rax"));
            s.append(generateInstruction("movq", "%rax", resultLocation));
          } else {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("subq", rightLocation, resultLocation));
          }
        } else {
          s.append(generateInstruction("movq", leftLocation, "%rax"));
          s.append(generateInstruction("subq", rightLocation, "%rax"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case MULTIPLY:
        if (resultInRegister) {
          if (leftInRegister && leftLocation.equals(resultLocation)) {
            s.append(generateInstruction("imulq", rightLocation, resultLocation));
          } else if (rightInRegister && rightLocation.equals(resultLocation)) {
            s.append(generateInstruction("imulq", leftLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("imulq", rightLocation, resultLocation));
          }
        } else {
          s.append(generateInstruction("movq", leftLocation, "%rax"));
          s.append(generateInstruction("imulq", rightLocation, "%rax"));
          s.append(generateInstruction("movq", "%rax", resultLocation));
        }
        break;
      case DIVIDE: // TODO(rbd): Can be further optimized.
        s.append(generateInstruction("movq", leftLocation, "%rax"));
        s.append(generateInstruction("cqto"));
        if (binary.getRight() instanceof LLConstantDeclaration) {
          s.append(generateInstruction("movq", rightLocation, "%r10"));
          s.append(generateInstruction("idivq", "%r10"));
        } else {
          s.append(generateInstruction("idivq", rightLocation));
        }
        s.append(generateInstruction("movq", "%rax", resultLocation));
        break;
      case MODULUS: // TODO(rbd): Can be further optimized.
        s.append(generateInstruction("movq", leftLocation, "%rax"));
        s.append(generateInstruction("cqto"));
        if (binary.getRight() instanceof LLConstantDeclaration) {
          s.append(generateInstruction("movq", rightLocation, "%r10"));
          s.append(generateInstruction("idivq", "%r10"));
        } else {
          s.append(generateInstruction("idivq", rightLocation));
        }
        s.append(generateInstruction("movq", "%rdx", resultLocation));
        break;
      case SHIFT_LEFT:
        if (resultInRegister) {
          if (right instanceof LLConstantDeclaration) {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("shlq", rightLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", rightLocation, "%rcx"));
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("shlq", "%cl", resultLocation));
          }
        } else {
          if (right instanceof LLConstantDeclaration) {
            s.append(generateInstruction("movq", leftLocation, "%rax"));
            s.append(generateInstruction("shlq", rightLocation, "%rax"));
            s.append(generateInstruction("movq", "%rax", resultLocation));
          } else {
            s.append(generateInstruction("movq", rightLocation, "%rcx"));
            s.append(generateInstruction("movq", leftLocation, "%rax"));
            s.append(generateInstruction("shlq", "%cl", "%rax"));
            s.append(generateInstruction("movq", "%rax", resultLocation));
          }
        }
        break;
      case SHIFT_RIGHT:
        if (resultInRegister) {
          if (right instanceof LLConstantDeclaration) {
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("shrq", rightLocation, resultLocation));
          } else {
            s.append(generateInstruction("movq", rightLocation, "%rcx"));
            s.append(generateInstruction("movq", leftLocation, resultLocation));
            s.append(generateInstruction("shrq", "%cl", resultLocation));
          }
        } else {
          if (right instanceof LLConstantDeclaration) {
            s.append(generateInstruction("movq", leftLocation, "%rax"));
            s.append(generateInstruction("shrq", rightLocation, "%rax"));
            s.append(generateInstruction("movq", "%rax", resultLocation));
          } else {
            s.append(generateInstruction("movq", rightLocation, "%rcx"));
            s.append(generateInstruction("movq", leftLocation, "%rax"));
            s.append(generateInstruction("shrq", "%cl", "%rax"));
            s.append(generateInstruction("movq", "%rax", resultLocation));
          }
        }
        break;
    }

    return s.toString();
  }

  // Noah: DONE
  public static String generateUnary(LLUnary unary) {
    // movq <expression.location()>, %rax
    // <type> %rax
    // movq %rax, <result.location()>

    StringBuilder s = new StringBuilder();

    final boolean expressionInRegister = unary.useInRegister(unary.getExpression());
    final boolean resultInRegister = unary.defInRegister();

    final String expressionLocation = unary.getUseWebLocation(unary.getExpression());
    final String resultLocation = unary.getDefWebLocation();

    if (unary.getType().equals(UnaryExpressionType.NEGATE)) {
      if (resultInRegister) {
        s.append(generateInstruction("movq", expressionLocation, resultLocation));
        s.append(generateInstruction("negq", resultLocation));
      } else {
        s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));
        s.append(generateInstruction("negq", "%rax"));
        s.append(generateInstruction("movq", "%rax", unary.getResult().location()));
      }
    } else if (unary.getType().equals(UnaryExpressionType.NOT)) {
      if (expressionInRegister) {
        s.append(generateInstruction("xorq", "%rax", "%rax"));
        s.append(generateInstruction("testq", expressionLocation, expressionLocation));
        s.append(generateInstruction("sete", "%al"));
        s.append(generateInstruction("movq", "%rax", resultLocation));
      } else {
        s.append(generateInstruction("movq", expressionLocation, "%r10"));
        s.append(generateInstruction("xorq", "%rax", "%rax"));
        s.append(generateInstruction("testq", "%r10", "%r10"));
        s.append(generateInstruction("sete", "%al"));
        s.append(generateInstruction("movq", "%rax", resultLocation));
      }
    } else if (unary.getType().equals(UnaryExpressionType.INCREMENT)) {
      if (resultInRegister) {
        s.append(generateInstruction("movq", expressionLocation, resultLocation));
        s.append(generateInstruction("incq", resultLocation));
      } else {
        s.append(generateInstruction("movq", unary.getExpression().location(), "%rax"));
        s.append(generateInstruction("incq", "%rax"));
        s.append(generateInstruction("movq", "%rax", unary.getResult().location()));
      }
    } else if (unary.getType().equals(UnaryExpressionType.DECREMENT)) {
      if (resultInRegister) {
        s.append(generateInstruction("movq", expressionLocation, resultLocation));
        s.append(generateInstruction("decq", resultLocation));
      } else {
        s.append(generateInstruction("movq", expressionLocation, "%rax"));
        s.append(generateInstruction("decq", "%rax"));
        s.append(generateInstruction("movq", "%rax", resultLocation));
      }
    }

    return s.toString();
  }

  // Phil: DONE
  public static String generateCompare(LLCompare compare) {
    StringBuilder s = new StringBuilder();

    final LLDeclaration left = compare.getLeft();
    final LLDeclaration right = compare.getRight();

    final boolean leftInRegister = compare.useInRegister(left);
    final boolean rightInRegister = compare.useInRegister(right);

    final String leftLocation = compare.getUseWebLocation(left);
    final String rightLocation = compare.getUseWebLocation(right);

    if (leftInRegister || rightInRegister) {
      s.append(generateInstruction("cmpq", rightLocation, leftLocation));
    } else {
      s.append(generateInstruction("movq", leftLocation, "%rax"));
      s.append(generateInstruction("cmpq", rightLocation, "%rax"));
    }

    return s.toString();
  }

  // Robert: DONE
  public static String generateLoadScalar(LLLoadScalar loadScalar) {
    final StringBuilder s = new StringBuilder();

    final LLDeclaration declaration = loadScalar.getDeclaration();

    final boolean resultInRegister = loadScalar.defInRegister();
    final boolean declarationInRegister = loadScalar.useInRegister(declaration);

    final String resultLocation = loadScalar.getDefWebLocation();
    final String declarationLocation = loadScalar.getUseWebLocation(declaration);

    if (resultInRegister || declarationInRegister) {
      s.append(generateInstruction("movq", declarationLocation, resultLocation));
    } else {
      s.append(generateInstruction("movq", declarationLocation, "%rax"));
      s.append(generateInstruction("movq", "%rax", resultLocation));
    }

    return s.toString();
  }

  // Noah: DONE
  public static String generateLoadArray(LLLoadArray loadArray) {
    StringBuilder s = new StringBuilder();

    final boolean indexInRegister = loadArray.useInRegister(loadArray.getIndex());
    final boolean resultInRegister = loadArray.defInRegister();

    final String indexLocation = loadArray.getUseWebLocation(loadArray.getIndex());
    final String resultLocation = loadArray.getDefWebLocation();

    if (indexInRegister && resultInRegister) {
      s.append(generateInstruction("movq", loadArray.getLocation().index(indexLocation), resultLocation));
    } else if (indexInRegister) {
      s.append(generateInstruction("movq", loadArray.getLocation().index(indexLocation), "%rax"));
      s.append(generateInstruction("movq", "%rax", resultLocation));
    } else if (resultInRegister) {
      s.append(generateInstruction("movq", indexLocation, "%r10"));
      s.append(generateInstruction("movq", loadArray.getLocation().index("%r10"), resultLocation));
    } else {
      s.append(generateInstruction("movq", indexLocation, "%r10"));
      s.append(generateInstruction("movq", loadArray.getLocation().index("%r10"), "%rax"));
      s.append(generateInstruction("movq", "%rax", resultLocation));
    }

    return s.toString();
  }

  // Phil: TODO
  public static String generateInternalCall(LLInternalCall internalCall) {
    StringBuilder s = new StringBuilder();

    List<String> registers = List.of("%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9");
    List<LLDeclaration> arguments = internalCall.getArguments();

    // TODO: (same comment as in external) Use the webs to limit the amount of pushes/pops
    int totalPushed = 0;
    for (String register : Registers.CALLER_SAVED) {
      if (!internalCall.getDefWebLocation().equals(register)) {
        s.append(generateInstruction("pushq", register));
        totalPushed++;
      }
    }

    // NOTE(rbd): Not necessary because of precoloring.
    // for (int i = 0; i < registers.size() && i < arguments.size(); ++i) {
    //   s.append(generateInstruction("movq", internalCall.getUseWebLocation(arguments.get(i)), registers.get(i)));
    // }

    if (arguments.size() > registers.size()) {
      totalPushed += (arguments.size() - registers.size());
    }

    if (totalPushed % 2 != 0) {
      s.append(generateInstruction("subq", "$8", "%rsp"));
    }

    for (int i = arguments.size() - 1; i > 5; --i) {
      s.append(generateInstruction("pushq", internalCall.getUseWebLocation(arguments.get(i))));
    }

    s.append(generateInstruction("callq", internalCall.getDeclaration().location()));

    int size = 0;
    if (totalPushed % 2 != 0) {
      size += 8;
    }
    if (arguments.size() > registers.size()) {
      size += (arguments.size() - registers.size()) * 8;
    }
    if (size > 0) {
      s.append(generateInstruction("addq", "$"+size, "%rsp"));
    }

    s.append(generateInstruction("movq", "%rax", internalCall.getDefWebLocation()));

    for (int i = Registers.CALLER_SAVED.size() - 1; i >= 0; --i) {
      if (!internalCall.getDefWebLocation().equals(Registers.CALLER_SAVED.get(i))) {
        s.append(generateInstruction("popq", Registers.CALLER_SAVED.get(i)));
      }
    }

    return s.toString();
  }

  // Robert: TODO
  public static String generateExternalCall(LLExternalCall externalCall) {
    StringBuilder s = new StringBuilder();

    List<String> registers = List.of("%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9");
    List<LLDeclaration> arguments = externalCall.getArguments();

    // TODO(rbd): Use the webs to limit the amount of pushes/pops
    int totalPushed = 0;
    for (String register : Registers.CALLER_SAVED) {
      if (!externalCall.getDefWebLocation().equals(register)) {
        s.append(generateInstruction("pushq", register));
        totalPushed++;
      }
    }

    // NOTE(rbd): Not necessary because of precoloring.
    // for (int i = 0; i < registers.size() && i < arguments.size(); ++i) {
    //   if (arguments.get(i) instanceof LLStringLiteralDeclaration stringLiteralDeclaration) {
    //     s.append(generateInstruction("leaq", arguments.get(i).location()+"(%rip)", registers.get(i)));
    //   } else {
    //     s.append(generateInstruction("movq", externalCall.getUseWebLocation(arguments.get(i)), registers.get(i)));
    //   }
    // }

    if (arguments.size() > registers.size()) {
      totalPushed += (arguments.size() - registers.size());
    }

    if (totalPushed % 2 != 0) {
      s.append(generateInstruction("subq", "$8", "%rsp"));
    }

    for (int i = arguments.size() - 1; i > 5; --i) {
      s.append(generateInstruction("pushq", externalCall.getUseWebLocation(arguments.get(i))));
    }

    s.append(generateInstruction("callq", externalCall.getDeclaration().location()));

    int size = 0;
    if (totalPushed % 2 != 0) {
      size += 8;
    }
    if (arguments.size() > registers.size()) {
      size += (arguments.size() - registers.size()) * 8;
    }
    if (size > 0) {
      s.append(generateInstruction("addq", "$"+size, "%rsp"));
    }

    s.append(generateInstruction("movq", "%rax", externalCall.getDefWebLocation()));

    for (int i = Registers.CALLER_SAVED.size() - 1; i >= 0; --i) {
      if (!externalCall.getDefWebLocation().equals(Registers.CALLER_SAVED.get(i))) {
        s.append(generateInstruction("popq", Registers.CALLER_SAVED.get(i)));
      }
    }

    return s.toString();
  }

  // Noah: DONE
  public static String generateLength(LLLength length) {
    StringBuilder s = new StringBuilder();

    s.append(generateInstruction("movq", "$"+length.getDeclaration().getLength(), length.getDefWebLocation()));

    return s.toString();
  }

  // Phil: DONE
  public static String generateIntegerLiteral(LLIntegerLiteral integerLiteral) {
    StringBuilder s = new StringBuilder();

    s.append(generateInstruction("movq", "$"+integerLiteral.getValue(), integerLiteral.getDefWebLocation()));

    return s.toString();
  }

  // Robert: DONE
  public static String generateStringLiteral(LLStringLiteral stringLiteral) {
    final StringBuilder s = new StringBuilder();

    final LLDeclaration declaration = stringLiteral.getDeclaration();

    final boolean resultInRegister = stringLiteral.defInRegister();

    final String resultLocation = stringLiteral.getDefWebLocation();
    final String declarationLocation = stringLiteral.getUseWebLocation(declaration);

    if (resultInRegister) {
      s.append(generateInstruction("leaq", declarationLocation+"(%rip)", resultLocation));
    } else {
      s.append(generateInstruction("leaq", declarationLocation+"(%rip)", "%rax"));
      s.append(generateInstruction("movq", "%rax", resultLocation));
    }

    return s.toString();
  }

  // Noah: DONE
  public static String generateCopy(LLCopy copy) {
    StringBuilder s = new StringBuilder();

    final boolean inputInRegister = copy.useInRegister(copy.getInput());
    final boolean resultInRegister = copy.defInRegister();

    final String inputLocation = copy.getUseWebLocation(copy.getInput());
    final String resultLocation = copy.getDefWebLocation();

    if (resultInRegister || inputInRegister) {
      s.append(generateInstruction("movq", inputLocation, resultLocation));
    } else {
      s.append(generateInstruction("movq", inputLocation, "%rax"));
      s.append(generateInstruction("movq", "%rax", resultLocation));
    }


    return s.toString();
  }

}
