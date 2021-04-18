package edu.mit.compilers.opt;

public class CycleDetectedException extends Exception {

  static final long serialVersionUID = 0L;

  public CycleDetectedException(String message) {
    super(message);
  }

}
