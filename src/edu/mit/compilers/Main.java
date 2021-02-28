package edu.mit.compilers;

import java.util.List;

class Main {

  public static void main(String[] args) {
    Lexer lexer = new Lexer();
    try {
      List<Token> tokens = lexer.lex("0Xace");
      System.out.println(tokens.toString());
    } catch (LexerException lexerException) {
      System.out.println(lexerException);
    }
  }

}
