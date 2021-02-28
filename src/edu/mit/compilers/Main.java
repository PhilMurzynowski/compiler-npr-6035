package edu.mit.compilers;

import java.util.List;

class Main {

  public static void main(String[] args) {
    Lexer lexer = new Lexer();
    try {
      List<Token> tokens = lexer.lex(" abcdefg\n Rinard\n martin_rinard\n six_dot_035\n _foo_\n ");
      System.out.println(tokens.toString());
    } catch (LexerException lexerException) {
      System.out.println(lexerException);
    }
  }

}
