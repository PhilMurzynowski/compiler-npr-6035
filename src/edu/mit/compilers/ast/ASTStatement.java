package edu.mit.compilers.ast;

public interface ASTStatement extends ASTNode {

  public static interface Visitor<T> {

    T visit(ASTIDAssignStatement idAssignStatement);

    T visit(ASTAssignStatement assignStatement);

    T visit(ASTCompoundAssignStatement compoundAssignStatement);

    T visit(ASTMethodCallStatement methodCallStatement);

    T visit(ASTIfStatement ifStatement);

    T visit(ASTForStatement forStatement);

    T visit(ASTWhileStatement whileStatement);

    T visit(ASTReturnStatement returnStatement);

    T visit(ASTBreakStatement breakStatement);

    T visit(ASTContinueStatement continueStatement);

  }

  public <T> T accept(ASTStatement.Visitor<T> visitor);

}
