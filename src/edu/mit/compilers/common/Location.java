package edu.mit.compilers.common;

import java.util.Objects;

import static edu.mit.compilers.common.Utilities.indent;

public class Location {

  private final int line;
  private final int column;

  private Location(int line, int column) {
    this.line = line;
    this.column = column;
  }

  public static Location start() {
    return new Location(1, 1);
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public Location incrementLine() {
    return new Location(line + 1, 1);
  }

  public Location incrementColumn() {
    return new Location(line, column + 1);
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("Location {\n");
    s.append(indent(depth + 1) + "line: " + line + ",\n");
    s.append(indent(depth + 1) + "column: " + column + ",\n");
    s.append(indent(depth) + "}");
    return s.toString();
  }

  @Override
  public String toString() {
    return line + ":" + column;
  }

  public boolean equals(Location that) {
    return (line == that.line)
      && (column == that.column);
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Location && equals((Location)that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(line, column);
  }

}
