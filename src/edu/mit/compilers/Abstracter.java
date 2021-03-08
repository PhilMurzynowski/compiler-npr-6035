package edu.mit.compilers;

import java.util.List;

class Abstracter {

  // Program -> ImportDeclaration* FieldMethodDeclaration?
  public ASTProgram abstractProgram(PTNode program) {
    final ASTProgram.Builder builder = new ASTProgram.Builder();

    final List<PTNode> children = program.getChildren();

    for (int i = 0; i < children.size() && children.get(i).is(PTNonterminal.Type.IMPORT_DECLARATION); ++i) {
      builder.addImportDeclaration(abstractImportDeclaration(children.get(i)));
    }

    if (children.get(children.size() - 1).is(PTNonterminal.Type.FIELD_METHOD_DECLARATION)) {
      abstractFieldMethodDeclaration(builder, children.get(children.size() - 1));
    }

    return builder.build();
  }

  // ImportDeclaration -> IMPORT IDENTIFIER SEMICOLON
  private ASTImportDeclaration abstractImportDeclaration(PTNode importDeclaration) {
    final ASTImportDeclaration.Builder builder = new ASTImportDeclaration.Builder();

    final List<PTNode> children = importDeclaration.getChildren();

    builder.withIdentifier(children.get(1).getText());
    
    return builder.build();
  }

  // FieldMethodDeclaration -> (INT | BOOL) IDENTIFIER (FieldDeclaration FieldMethodDeclaration? | MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*) | VOID IDENTIFIER MethodDeclaration ((INT | BOOL | VOID) IDENTIFIER MethodDeclaration)*
  private void abstractFieldMethodDeclaration(ASTProgram.Builder builder, PTNode fieldMethodDeclaration) {
    final List<PTNode> children = fieldMethodDeclaration.getChildren();

    if (children.get(2).is(PTNonterminal.Type.FIELD_DECLARATION)) {
      final ASTFieldDeclaration.Builder fieldBuilder = new ASTFieldDeclaration.Builder();

      if (children.get(0).is(Token.Type.INT)) {
        fieldBuilder.withType(ASTFieldDeclaration.Type.INTEGER);
      } else /* if (children.get(0).is(Token.Type.BOOL)) */ {
        fieldBuilder.withType(ASTFieldDeclaration.Type.BOOLEAN);
      }

      final ASTFieldDeclaration.Identifier.Builder identifierBuilder = new ASTFieldDeclaration.Identifier.Builder();

      identifierBuilder.withIdentifier(children.get(1).getText());

      builder.addFieldDeclaration(abstractFieldDeclaration(fieldBuilder, identifierBuilder, children.get(2)));

      if (children.get(3).is(PTNonterminal.Type.FIELD_METHOD_DECLARATION)) {
        abstractFieldMethodDeclaration(builder, children.get(3));
      }
    } else /* if (children.get(2).is(PTNonterminal.Type.METHOD_DECLARATION)) */ {
      ASTMethodDeclaration.Builder methodBuilder = new ASTMethodDeclaration.Builder();

      if (children.get(0).is(Token.Type.INT)) {
        methodBuilder.withType(ASTMethodDeclaration.Type.INTEGER);
      } else if (children.get(0).is(Token.Type.BOOL)) {
        methodBuilder.withType(ASTMethodDeclaration.Type.BOOLEAN);
      } else /* if (children.get(0).is(Token.Type.VOID)) */ {
        methodBuilder.withType(ASTMethodDeclaration.Type.VOID);
      }

      methodBuilder.withIdentifier(children.get(1).getText());

      builder.addMethodDeclaration(abstractMethodDeclaration(methodBuilder, children.get(2)));

      for (int i = 3; i < children.size(); i += 3) {
        methodBuilder = new ASTMethodDeclaration.Builder();

        if (children.get(i).is(Token.Type.INT)) {
          methodBuilder.withType(ASTMethodDeclaration.Type.INTEGER);
        } else if (children.get(i).is(Token.Type.BOOL)) {
          methodBuilder.withType(ASTMethodDeclaration.Type.BOOLEAN);
        } else /* if (children.get(i).is(Token.Type.VOID)) */ {
          methodBuilder.withType(ASTMethodDeclaration.Type.VOID);
        }

        methodBuilder.withIdentifier(children.get(i + 1).getText());

        builder.addMethodDeclaration(abstractMethodDeclaration(methodBuilder, children.get(i + 2)));
      }
    }
  }

  // FieldDeclaration -> (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)? (COMMA IDENTIFIER (LEFT_SQUARE (DECIMAL | HEXADECIMAL) RIGHT_SQUARE)?)* SEMICOLON
  private ASTFieldDeclaration abstractFieldDeclaration(ASTFieldDeclaration.Builder builder, ASTFieldDeclaration.Identifier.Builder identifierBuilder, PTNode fieldDeclaration) {
    final List<PTNode> children = fieldDeclaration.getChildren();

    int i = 0;

    if (children.get(i).is(Token.Type.LEFT_SQUARE)) {
      identifierBuilder.withLength(Integer.parseInt(children.get(i + 1).getText()));
      i += 3;
    }

    builder.addIdentifier(identifierBuilder.build());

    while (children.get(i).is(Token.Type.COMMA)) {
      identifierBuilder = new ASTFieldDeclaration.Identifier.Builder();

      identifierBuilder.withIdentifier(children.get(i + 1).getText());
      i += 2;

      if (children.get(i).is(Token.Type.LEFT_SQUARE)) {
        identifierBuilder.withLength(Integer.parseInt(children.get(i + 1).getText()));
        i += 3;
      }

      builder.addIdentifier(identifierBuilder.build());
    }

    return builder.build();
  }

  // MethodDeclaration -> LEFT_ROUND ((INT | BOOL) IDENTIFIER (COMMA (INT | BOOL) IDENTIFIER)*)? RIGHT_ROUND Block
  private ASTMethodDeclaration abstractMethodDeclaration(ASTMethodDeclaration.Builder builder, PTNode methodDeclaration) {
    final List<PTNode> children = methodDeclaration.getChildren();

    int i = 0;

    if (children.get(i + 1).in(Token.Type.INT, Token.Type.BOOL)) {
      ASTMethodDeclaration.Argument.Builder argumentBuilder = new ASTMethodDeclaration.Argument.Builder();

      if (children.get(i + 1).is(Token.Type.INT)) {
        argumentBuilder.withType(ASTMethodDeclaration.Argument.Type.INTEGER);
      } else /* if (children.get(i + 1).is(Token.Type.BOOL)) */ {
        argumentBuilder.withType(ASTMethodDeclaration.Argument.Type.BOOLEAN);
      }

      argumentBuilder.withIdentifier(children.get(i + 2).getText());

      builder.addArgument(argumentBuilder.build());

      for (i = 3; children.get(i).is(Token.Type.COMMA); i += 3) {
        argumentBuilder = new ASTMethodDeclaration.Argument.Builder();

        if (children.get(i + 1).is(Token.Type.INT)) {
          argumentBuilder.withType(ASTMethodDeclaration.Argument.Type.INTEGER);
        } else /* if (children.get(i + 1).is(Token.Type.BOOL)) */ {
          argumentBuilder.withType(ASTMethodDeclaration.Argument.Type.BOOLEAN);
        }

        argumentBuilder.withIdentifier(children.get(i + 2).getText());

        builder.addArgument(argumentBuilder.build());
      }
    }

    builder.withBlock(abstractBlock(children.get(i + 1)));

    return builder.build();
  }

  // Block -> LEFT_CURLY ((INT | BOOL) IDENTIFIER FieldDeclaration)* Statement* RIGHT_CURLY
  private ASTBlock abstractBlock(PTNode block) {
    throw new RuntimeException("not implemented");
  }

}
