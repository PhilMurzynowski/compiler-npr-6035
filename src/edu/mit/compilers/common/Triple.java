package edu.mit.compilers.common;

import java.util.Objects;

public class Triple<T, U, V> {

  public final T first;
  public final U second;
  public final V third;

  public Triple(T first, U second, V third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Triple && sameValue((Triple) that);
  }

  private boolean sameValue(Triple that) {
    return this.first.equals(that.first)
        && this.second.equals(that.second)
        && this.third.equals(that.third);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second, third);
  }

  @Override
  public String toString() {
    return first + ":" + second + ":" + third;
  }
}
