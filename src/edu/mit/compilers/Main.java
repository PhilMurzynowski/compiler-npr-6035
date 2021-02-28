package edu.mit.compilers;

import java.util.List;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;

class Main {

  private static String tokenString(Token token) {
    StringBuilder output = new StringBuilder();
    output.append(token.getLine());
    switch (token.getType()) {
      case CHARACTER:
        output.append(" CHARLITERAL");
        break;
      case DECIMAL:
      case HEXADECIMAL:
        output.append(" INTLITERAL");
        break;
      case TRUE:
      case FALSE:
        output.append(" BOOLEANLITERAL");
        break;
      case STRING:
        output.append(" STRINGLITERAL");
        break;
      case IDENTIFIER:
        output.append(" IDENTIFIER");
        break;
      default:
        break;
    }
    output.append(" " + token.getText());
    return output.toString();
  }

  private static String lexerExceptionString(String filename, String input, LexerException lexerException) {
    StringBuilder output = new StringBuilder();
    output.append(filename);
    output.append(":" + lexerException.getLine());
    output.append(":" + lexerException.getColumn());
    output.append(": " + lexerException.getType());
    output.append(": " + lexerException.getMessage() + ":\n\n");
    output.append(input.split("\n", -1)[lexerException.getLine() - 1] + "\n");
    output.append(" ".repeat(lexerException.getColumn() - 1) + "^");
    return output.toString();
  }

  private static void lex(String filename, String input, PrintStream outputStream) {
    Lexer lexer = new Lexer();
    try {
      List<Token> tokens = lexer.lex(input);
      for (Token token : tokens) {
        outputStream.println(tokenString(token));
      }
    } catch (LexerException lexerException) {
      outputStream.println(lexerExceptionString(filename, input, lexerException));
      System.exit(1);
    }
  }

  // https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
  private static String streamToString(InputStream inputStream) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    for (int length; (length = inputStream.read(buffer)) != -1; ) {
      result.write(buffer, 0, length);
    }
    return result.toString("UTF-8");
  }

  public static void main(String[] args) {
    try {
      CLI.parse(args, new String[0]);
      String input = streamToString(CLI.infile == null ? System.in : new FileInputStream(CLI.infile));
      PrintStream outputStream = CLI.outfile == null ? System.out : new PrintStream(new FileOutputStream(CLI.outfile));
      switch (CLI.target) {
        case SCAN:
          lex("filename", input, outputStream);
          break;
        case PARSE:
        case DEFAULT:
          break;
        default:
          break;
      }
    } catch (Exception exception) {
      System.out.print(exception);
      System.exit(-1);
    }
  }

}
