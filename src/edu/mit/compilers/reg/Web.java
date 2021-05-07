package edu.mit.compilers.reg;

import java.util.*;

import static edu.mit.compilers.common.Utilities.indent;

public class Web {

  public static final String SPILL = "SPILL";

  private static long counter = 0;

  private final long index;
  private Optional<String> location;

  public Web() {
    this.index = counter++;
    this.location = Optional.empty();
  }

  public Web(final String precolor) {
    this.index = counter++;
    this.location = Optional.of(precolor);
  }

  public long getIndex() {
    return index;
  }

  public void setLocation(String location) {
    if (this.location.isPresent()) {
      throw new RuntimeException("location for web has already been set");
    } else {
      this.location = Optional.of(location);
    }
  }

  public String getLocation() {
    if (this.location.isPresent()) {
      return location.get();
    } else {
      throw new RuntimeException("location for web has not been set");
    }
  }

  public boolean hasLocation() {
    return this.location.isPresent();
  }

  public String debugString(int depth) {
    StringBuilder s = new StringBuilder();
    s.append("Web {\n");
    s.append(indent(depth + 1) + "index: " + index + ",\n");
    if (location.isPresent()) {
      s.append(indent(depth + 1) + "location: " + location + ",\n");
    }
    s.append(indent(depth) + "}");
    return s.toString();
  }


  @Override
  public String toString() {
    return debugString(0);
  }

}
