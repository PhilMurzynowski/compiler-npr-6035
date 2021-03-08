package edu.mit.compilers;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import static edu.mit.compilers.Utilities.indent;

class PTNonterminal implements PTNode {

  public enum Type {
    START,                        // Program

    PROGRAM,                      // ImportDeclaration* FieldDeclaration* MethodDeclaration*

    IMPORT_DECLARATION,           // IMPORT IDENTIFIER SEMICOLON
    FIELD_DECLARATION,            // (INT | BOOL) FieldIdentifierDeclaration (COMMA FieldIdentifierDeclaration)* SEMICOLON
    METHOD_DECLARATION,           // (INT | BOOL | VOID) IDENTIFIER LEFT_ROUND (ArgumentDeclaration (COMMA ArgumentDeclaration)*)? RIGHT_ROUND Block

    FIELD_IDENTIFIER_DECLARATION, // IDENTIFIER (LEFT_SQUARE IntegerLiteral RIGHT_SQUARE)?
    ARGUMENT_DECLARATION,         // (INT | BOOL) IDENTIFIER

    BLOCK,                        // LEFT_CURLY FieldDeclaration* Statement* RIGHT_CURLY

    STATEMENT,                    // AssignStatement | CompoundAssignStatement | MethodCallStatement | IfStatement | ForStatement | WhileStatement | ReturnStatement | BreakStatement | ContinueStatement
    ASSIGN_STATEMENT,             // AssignExpression SEMICOLON
    COMPOUND_ASSIGN_STATEMENT,    // CompoundAssignExpression SEMICOLON
    METHOD_CALL_STATEMENT,        // MethodCallExpression SEMICOLON
    IF_STATEMENT,                 // IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
    FOR_STATEMENT,                // FOR LEFT_ROUND AssignExpression SEMICOLON Expression SEMICOLON CompoundAssignExpression RIGHT_ROUND Block
    WHILE_STATEMENT,              // WHILE LEFT_ROUND Expression RIGHT_ROUND Block
    RETURN_STATEMENT,             // RETURN (Expression)? SEMICOLON
    BREAK_STATEMENT,              // BREAK SEMICOLON
    CONTINUE_STATEMENT,           // CONTINUE SEMICOLON

    ASSIGN_EXPRESSION,            // LocationExpression EQUAL Expression
    COMPOUND_ASSIGN_EXPRESSION,   // LocationExpression ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS))

    EXPRESSION,                   // OrExpression
    OR_EXPRESSION,                // AndExpression (VERTICAL_VERTICAL AndExpression)*
    AND_EXPRESSION,               // EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
    EQUALITY_EXPRESSION,          // RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
    RELATIONAL_EXPRESSION,        // AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
    ADDITIVE_EXPRESSION,          // MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
    MULTIPLICATIVE_EXPRESSION,    // NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
    NOT_EXPRESSION,               // BANG* NegationExpression
    NEGATION_EXPRESSION,          // MINUS* UnitExpression
    UNIT_EXPRESSION,              // LocationExpression | MethodCallExpression | LengthExpression | Literal | LEFT_ROUND Expression RIGHT_ROUND
    LOCATION_EXPRESSION,          // IDENTIFIER (LEFT_SQUARE Expression RIGHT_SQUARE)?
    METHOD_CALL_EXPRESSION,       // IDENTIFIER LEFT_ROUND (Argument (COMMA Argument)*)? RIGHT_ROUND 
    LENGTH_EXPRESSION,            // LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND

    ARGUMENT,                     // Expression | StringLiteral

    LITERAL,                      // IntegerLiteral | CharacterLiteral | BooleanLiteral
    INTEGER_LITERAL,              // DECIMAL | HEXADECIMAL
    CHARACTER_LITERAL,            // CHARACTER
    BOOLEAN_LITERAL,              // TRUE | FALSE

    STRING_LITERAL,               // STRING

    EOS,                          // NOTE(rbd): Special end-of-stream sentinel type used by Abstracter.Peekable
  }

  private final Type type;
  private final List<PTNode> children;

  private PTNonterminal(Type type, List<PTNode> children) {
    this.type = type;
    this.children = children;
  }

  public static class Builder {

    private Type type;
    private final List<PTNode> children;

    public Builder() {
      this.children = new ArrayList<>();
    }

    public Builder(Type type) {
      this();
      this.type = type;
    }

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder addChild(PTNode child) {
      children.add(child);
      return this;
    }

    public PTNonterminal build() {
      return new PTNonterminal(type, List.copyOf(children));
    }

  }

  @Override
  public boolean is(Token.Type ...tokenTypes) {
    return false;
  }

  @Override
  public boolean is(Type ...types) {
    for (Type type : types) {
      if (this.type == type) {
        return true;
      }
    }
    return false;
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
