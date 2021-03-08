package edu.mit.compilers;

import java.util.List;

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

  public E peek(int offset) {
    if (index + offset >= list.size()) {
      return list.get(list.size() - 1);
    } else {
      return list.get(index + offset);
    }
  }

  public E peek() {
    return peek(0);
  }

  public E next() {
    if (index >= list.size()) {
      return list.get(list.size() - 1);
    } else {
      return list.get(index++);
    }
  }

}
