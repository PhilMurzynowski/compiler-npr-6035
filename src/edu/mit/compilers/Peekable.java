package edu.mit.compilers;

import java.util.List;
import java.util.NoSuchElementException;

class Peekable<E> {

  private final List<E> list;
  private int index;

  public static <E> Peekable<E> from(List<E> list) {
    return new Peekable<E>(list, 0);
  }

  private Peekable(List<E> list, int index) {
    this.list = list;
    this.index = index;
  }

  public boolean hasNext() {
    return index < list.size();
  }

  public E peek() {
    if (index >= list.size()) {
      throw new NoSuchElementException();
    }
    return list.get(index);
  }

  public E next() {
    if (index >= list.size()) {
      throw new NoSuchElementException();
    }
    return list.get(index++);
  }

}
