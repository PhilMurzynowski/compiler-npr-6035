package edu.mit.compilers.common;

import java.util.Optional;

/*
 * A custom class that holds one of two types.
 */

public final class Either<A, B> {

  private final Optional<A> a;
  private final Optional<B> b;

  private Either(Optional<A> a, Optional<B> b) {
    this.a = a;
    this.b = b;
  }

  public static <A, B> Either<A, B> left(A a_instance) {
    return new Either<>(Optional.of(a_instance), Optional.empty());  
  }

  public static <A, B> Either<A, B> right(B b_instance) {
    return new Either<>(Optional.empty(), Optional.of(b_instance));  
  }

  // NOTE: may want some typechecking with these projections
  
  public A left() {
    return this.a.get();
  }

  public B right() {
    return this.b.get();
  }

  public boolean isLeft() {
    return this.a.isPresent();
  }

  public boolean isRight() {
    return this.b.isPresent();
  }

}
