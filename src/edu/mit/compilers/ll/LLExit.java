package edu.mit.compilers.ll;

import java.util.Optional;

import static edu.mit.compilers.common.Utilities.indent;

public class LLExit implements LLInstruction {

  private final int exitCode;
  private final String message;

  public LLExit(int exitCode, String message) {
    this.exitCode = exitCode;
    this.message = message;
  }

  public int getExitCode() {
    return exitCode;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String prettyString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("exit " + exitCode + " " + message);
    return s.toString();
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("LLExit {\n");
    s.append(indent(depth + 1) + "exitCode: " + exitCode + ",\n");
    s.append(indent(depth + 1) + "message : " + message + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
