package edu.mit.compilers.common;

import java.util.Set;
import java.util.HashSet;

public class Utilities {

  public static String indent(int n) {
    return "  ".repeat(n);
  }

  @SafeVarargs
  public static <T> Set<T> union(Set<T> ...sets) {
    if (sets.length > 0) {
      final Set<T> union = new HashSet<>(sets[0]);

      for (int i = 1; i < sets.length; i++) {
        union.addAll(sets[i]);
      }

      return union;
    } else {
      return new HashSet<>();
    }
  }

  @SafeVarargs
  public static <T> Set<T> intersection(Set<T> ...sets) {
    if (sets.length > 0) {
      final Set<T> intersection = new HashSet<>(sets[0]);

      for (int i = 1; i < sets.length; i++) {
        intersection.retainAll(sets[i]);
      }

      return intersection;
    } else {
      return new HashSet<>();
    }
  }

}
