package edu.mit.compilers.ast;

public interface ASTStatement extends ASTNode {

  public static interface Visitor {

    void visit(ASTIDAssignStatement idAssignStatement);

    void visit(ASTAssignStatement assignStatement);

    void visit(ASTCompoundAssignStatement compoundAssignStatement);

    void visit(ASTMethodCallStatement methodCallStatement);

    void visit(ASTIfStatement ifStatement);

    void visit(ASTForStatement forStatement);

    void visit(ASTWhileStatement whileStatement);

    void visit(ASTReturnStatement returnStatement);

    void visit(ASTBreakStatement breakStatement);

    void visit(ASTContinueStatement continueStatement);

  }

  public void accept(ASTStatement.Visitor visitor);

}
