package edu.mit.compilers.common;

import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class TextLocation {

  private final int line;
  private final int column;

  private TextLocation(int line, int column) {
    this.line = line;
    this.column = column;
  }

  public static TextLocation start() {
    return new TextLocation(1, 1);
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public TextLocation incrementLine() {
    return new TextLocation(line + 1, 1);
  }

  public TextLocation incrementColumn() {
    return new TextLocation(line, column + 1);
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("TextLocation {\n");
    s.append(indent(depth + 1) + "line: " + line + ",\n");
    s.append(indent(depth + 1) + "column: " + column + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return line + ":" + column;
  }

  public boolean equals(TextLocation that) {
    return (line == that.line)
      && (column == that.column);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof TextLocation && equals((TextLocation)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(line, column);
  }

}
