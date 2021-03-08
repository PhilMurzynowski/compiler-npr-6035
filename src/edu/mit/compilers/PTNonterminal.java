package edu.mit.compilers;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import static edu.mit.compilers.Utilities.indent;

class PTNonterminal implements PTNode {

  public enum Type {
    START,                           // Program
    PROGRAM,                         // ImportDeclaration* FieldMethodDeclaration?
    IMPORT_DECLARATION,              // IMPORT IDENTIFIER SEMICOLON
    FIELD_METHOD_DECLARATION,        // (INT | BOOL) IDENTIFIER (FieldDeclaration FieldMethodDeclaration? | MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*) | VOID IDENTIFIER MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*
    FIELD_DECLARATION,               // (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)? (COMMA IDENTIFIER (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)?)* SEMICOLON
    METHOD_DECLARATION,              // LEFT_ROUND ((INT | BOOL) IDENTIFIER (COMMA (INT | BOOL) IDENTIFIER)*)? RIGHT_ROUND Block
    BLOCK,                           // LEFT_CURLY ((INT | BOOL) IDENTIFIER FieldDeclaration)* Statement* RIGHT_CURLY
    STATEMENT,                       // AssignMethodCallStatement | IfStatement | ForStatement | WhileStatement | ReturnStatement | BreakStatement | ContinueStatement
    ASSIGN_METHOD_CALL_STATEMENT,    // IDENTIFIER (AssignStatement | MethodCallStatement)
    ASSIGN_STATEMENT,                // LocationExpression? ((EQUAL | PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS)) SEMICOLON
    METHOD_CALL_STATEMENT,           // MethodCallExpression SEMICOLON
    IF_STATEMENT,                    // IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
    FOR_STATEMENT,                   // FOR LEFT_ROUND IDENTIFIER EQUAL Expression SEMICOLON Expression SEMICOLON IDENTIFIER LocationExpression? ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS)) RIGHT_ROUND Block
    WHILE_STATEMENT,                 // WHILE LEFT_ROUND Expression RIGHT_ROUND Block
    RETURN_STATEMENT,                // RETURN (Expression)? SEMICOLON
    BREAK_STATEMENT,                 // BREAK SEMICOLON
    CONTINUE_STATEMENT,              // CONTINUE SEMICOLON
    EXPRESSION,                      // OrExpression
    OR_EXPRESSION,                   // AndExpression (VERTICAL_VERTICAL AndExpression)*
    AND_EXPRESSION,                  // EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
    EQUALITY_EXPRESSION,             // RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
    RELATIONAL_EXPRESSION,           // AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
    ADDITIVE_EXPRESSION,             // MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
    MULTIPLICATIVE_EXPRESSION,       // NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
    NOT_EXPRESSION,                  // BANG* NegationExpression
    NEGATION_EXPRESSION,             // MINUS* UnitExpression
    UNIT_EXPRESSION,                 // LocationMethodCallExpression | LengthExpression | Literal | LEFT_ROUND Expression RIGHT_ROUND
    LOCATION_METHOD_CALL_EXPRESSION, // IDENTIFIER (LocationExpression? | MethodCallExpression)
    LOCATION_EXPRESSION,             // LEFT_SQUARE Expression RIGHT_SQUARE
    METHOD_CALL_EXPRESSION,          // LEFT_ROUND ((Expression | STRING) (COMMA (Expression | STRING))*)? RIGHT_ROUND 
    LENGTH_EXPRESSION,               // LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND
    LITERAL,                         // IntegerLiteral | CharacterLiteral | BooleanLiteral
    INTEGER_LITERAL,                 // DECIMAL | HEXADECIMAL
    CHARACTER_LITERAL,               // CHARACTER
    BOOLEAN_LITERAL,                 // TRUE | FALSE
  }

  private final Type type;
  private final List<PTNode> children;

  private PTNonterminal(Type type, List<PTNode> children) {
    this.type = type;
    this.children = children;
  }

  public static class Builder {

    private final Type type;
    private final List<PTNode> children;

    public Builder(Type type) {
      this.type = type;
      this.children = new ArrayList<PTNode>();
    }

    public Builder addChild(PTNode child) {
      children.add(child);
      return this;
    }

    public PTNonterminal build() {
      return new PTNonterminal(type, children);
    }

  }

  @Override
  public boolean is(Token.Type tokenType) {
    return false;
  }

  @Override
  public boolean in(Token.Type ...tokenTypes) {
    return false;
  }

  @Override
  public boolean is(Type type) {
    return this.type == type;
  }

  @Override
  public String getText() {
    StringBuilder s = new StringBuilder();
    for (PTNode child : children) {
      s.append(child.getText());
    }
    return s.toString();
  }

  @Override
  public List<PTNode> getChildren() {
    return children;
  }

  @Override
  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("PTNonterminal {\n");
    s.append(indent(depth + 1) + "type: " + type + ",\n");
    s.append(indent(depth + 1) + "children: [\n");
    for (PTNode child : children) {
      s.append(indent(depth + 2) + child.debugString(depth + 2) + ",\n");
    }
    s.append(indent(depth + 1) + "],\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return debugString(0);
  }

  public boolean equals(PTNonterminal that) {
    return (type.equals(that.type))
      && (children.equals(that.children));
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof PTNonterminal) && equals((PTNonterminal)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, children);
  }

};
