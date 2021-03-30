package edu.mit.compilers.ll;
import java.util.Optional;

public class LLReturn implements LLInstruction {

  private final Optional<LLDeclaration> expression;

  public LLReturn(Optional<LLDeclaration> expression) {
    this.expression = expression;
  }

  @Override
  public String debugString(int depth) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String toString() {
    return debugString(0);
  }

}
