package edu.mit.compilers.reg;

import java.util.*;

import static java.util.Map.entry;

public class Registers {

  public static final String RAX = "%rax";
  public static final String RBX = "%rbx";
  public static final String RCX = "%rcx";
  public static final String RDX = "%rdx";
  public static final String RDI = "%rdi";
  public static final String RSI = "%rsi";
  public static final String RSP = "%rsp";
  public static final String RBP = "%rbp";
  public static final String R8  = "%r8";
  public static final String R9  = "%r9";
  public static final String R10 = "%r10";
  public static final String R11 = "%r11";
  public static final String R12 = "%r12";
  public static final String R13 = "%r13";
  public static final String R14 = "%r14";
  public static final String R15 = "%r15";

  public static final String AL = "%al";
  public static final String BL = "%bl";
  public static final String CL = "%cl";
  public static final String DL = "%dl";
  public static final String SIL = "%sil";
  public static final String DIL = "%dil";
  public static final String SPL = "%spl";
  public static final String BPL = "%bpl";
  public static final String R8B = "%r8b";
  public static final String R9B = "%r9b";
  public static final String R10B = "%r10b";
  public static final String R11B = "%r11b";
  public static final String R12B = "%r12b";
  public static final String R13B = "%r13b";
  public static final String R14B = "%r14b";
  public static final String R15B = "%r15b";

  private static final Map<String, String> Q2B = Map.ofEntries(
    entry(RAX, AL),
    entry(RBX, BL),
    entry(RCX, CL),
    entry(RDX, DL),
    entry(RDI, SIL),
    entry(RSI, DIL),
    entry(RSP, SPL),
    entry(RBP, BPL),
    entry(R8, R8B),
    entry(R9, R9B),
    entry(R10, R10B),
    entry(R11, R11B),
    entry(R12, R12B),
    entry(R13, R13B),
    entry(R14, R14B),
    entry(R15, R15B)
  );

  public static String q2b(String reg) {
    return Q2B.get(reg);
  }
  
  public static final List<String> CALLER_SAVED = List.of(
    RAX,
    RCX, 
    RDX,
    RDI,
    RSI,
    RSP,
    R8,
    R9,
    R10,
    R11
  );

  public static final List<String> CALLEE_SAVED = List.of(
    RBX,
    RBP,
    R12,
    R13,
    R14,
    R15
  );

}
