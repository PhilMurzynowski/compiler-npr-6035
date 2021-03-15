package edu.mit.compilers;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.List;

import edu.mit.compilers.tk.*;
import edu.mit.compilers.pt.*;
import edu.mit.compilers.ast.*;
import edu.mit.compilers.ir.*;

class Main {

  private static String tokenString(Token token) {
    StringBuilder output = new StringBuilder();
    output.append(token.getTextLocation().getLine());
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
    output.append(":" + lexerException.getTextLocation().getLine());
    output.append(":" + lexerException.getTextLocation().getColumn());
    output.append(": " + lexerException.getType());
    output.append(": " + lexerException.getMessage() + ":\n\n");
    output.append(input.split("\n", -1)[lexerException.getTextLocation().getLine() - 1] + "\n");
    output.append(" ".repeat(lexerException.getTextLocation().getColumn() - 1) + "^");
    return output.toString();
  }

  private static void lex(String filename, String input, PrintStream outputStream) {
    Lexer.Result lexerResult = new Lexer().lexAll(input);

    for (Token token : lexerResult.getTokens()) {
      if (!token.is(Token.Type.EOF)) {
        outputStream.println(tokenString(token));
      }
    }

    if (lexerResult.hasExceptions()) {
      System.err.println("\n*** ERRORS ***\n");

      for (LexerException exception : lexerResult.getExceptions()) {
        System.err.println(lexerExceptionString(filename, input, exception));
      }

      System.exit(1);
    }
  }

  private static String parserExceptionString(String filename, String input, ParserException parserException) {
    StringBuilder output = new StringBuilder();
    output.append(filename);
    output.append(":" + parserException.getTextLocation().getLine());
    output.append(":" + parserException.getTextLocation().getColumn());
    output.append(": " + parserException.getType());
    output.append(": " + parserException.getMessage() + ":\n\n");
    output.append(input.split("\n", -1)[parserException.getTextLocation().getLine() - 1] + "\n");
    output.append(" ".repeat(parserException.getTextLocation().getColumn() - 1) + "^");
    return output.toString();
  }

  private static void parse(String filename, String input, PrintStream outputStream) {
    Lexer.Result lexerResult = new Lexer().lexAll(input);
    Parser.Result parserResult = new Parser().parseAll(lexerResult.getTokens());

    if (lexerResult.hasExceptions() || parserResult.hasExceptions()) {
      System.err.println("\n*** ERRORS ***\n");

      for (LexerException exception : lexerResult.getExceptions()) {
        System.err.println(lexerExceptionString(filename, input, exception));
      }

      for (ParserException exception : parserResult.getExceptions()) {
        System.err.println(parserExceptionString(filename, input, exception));
      }

      System.exit(1);
    }

  }

  // FIXME(rbd): I reeeaaally need to make an abstract CompilerException class and stop copy-pasting this...

  private static String semanticExceptionString(String filename, String input, SemanticException semanticException) {
    StringBuilder output = new StringBuilder();
    output.append(filename);
    output.append(":" + semanticException.getTextLocation().getLine());
    output.append(":" + semanticException.getTextLocation().getColumn());
    output.append(": " + semanticException.getType());
    output.append(": " + semanticException.getMessage() + ":\n\n");
    output.append(input.split("\n", -1)[semanticException.getTextLocation().getLine() - 1] + "\n");
    output.append(" ".repeat(semanticException.getTextLocation().getColumn() - 1) + "^");
    return output.toString();
  }

  private static void check(String filename, String input, PrintStream outputStream) {
    Lexer.Result lexerResult = new Lexer().lexAll(input);
    Parser.Result parserResult = new Parser().parseAll(lexerResult.getTokens());
    ASTProgram program = new Abstracter().abstractProgram(parserResult.getParseTree());
    List<SemanticException> semanticExceptions = program.accept(new ProgramChecker(new SymbolTable(), false, Optional.empty()));

    if (lexerResult.hasExceptions() || parserResult.hasExceptions() || !semanticExceptions.isEmpty()) {
      System.err.println("\n*** ERRORS ***\n");

      for (LexerException exception : lexerResult.getExceptions()) {
        System.err.println(lexerExceptionString(filename, input, exception));
      }

      for (ParserException exception : parserResult.getExceptions()) {
        System.err.println(parserExceptionString(filename, input, exception));
      }

      for (SemanticException exception : semanticExceptions) {
        System.err.println(semanticExceptionString(filename, input, exception));
      }

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
          lex(CLI.infile == null ? "STDIN" : CLI.infile, input, outputStream);
          break;
        case PARSE:
        case DEFAULT:
          parse(CLI.infile == null ? "STDIN" : CLI.infile, input, outputStream);
          break;
        case INTER:
          check(CLI.infile == null ? "STDIN" : CLI.infile, input, outputStream);
          break;
        default:
          break;
      }
    } catch (Exception exception) {
      System.err.print(exception);
      System.exit(-1);
    }
  }

}
