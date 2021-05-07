package edu.mit.compilers.reg;

import java.util.*;

public class Chain {

  private static int counter = 0;

  private int index;
  private Chain parent;
  private Optional<Web> web;
  private Set<Chain> interference;
  private final Optional<String> precolor;

  public Chain() {
    index = counter++;
    parent = this;
    web = Optional.empty();
    interference = new HashSet<>();
    precolor = Optional.empty();
  }

  public Chain(final String precolor) {
    index = counter++;
    parent = this;
    web = Optional.empty();
    interference = new HashSet<>();
    this.precolor = Optional.of(precolor);
  }

  public int getIndex() {
    return index;
  }

  public Chain find() {
    if (this.parent == this) {
      return this;
    } else {
      return this.parent.find();
    }
  }

  public void union(final Chain that) {
    final Chain thisSet = this.find();
    final Chain thatSet = that.find();
    assert thisSet.precolor.equals(thatSet.precolor) : "should only ever union chains that have the same precolor";
    thatSet.setParent(thisSet);
  }

  public void setParent(final Chain parent) {
    this.parent = parent;
  }

  public Web getWeb() {
    final Chain chainSet = find();
    if (chainSet.web.isEmpty()) {
      throw new RuntimeException("web does not exist for this chain set. did you implement union find?");
    } else {
      return chainSet.web.get();
    }
  }

  public boolean hasWeb() {
    final Chain chainSet = find();
    return chainSet.web.isPresent();
  }

  public void setWeb(final Web web) {
    assert this.parent == this : "can only set web if representative of the chain set";
    if (this.web.isPresent()) {
      throw new RuntimeException("web already exists. did you implement union find... correctly?");
    } else {
      this.web = Optional.of(web);
    }
  }

  public void addInterference(final Chain chain) {
    interference.add(chain);
  }

  public Set<Chain> getInterference() {
    return interference;
  }

  public boolean isPrecolored() {
    return precolor.isPresent();
  }

  public String getPrecolor() {
    if (precolor.isEmpty()) {
      throw new RuntimeException("chain is not precolored");
    } else {
      return precolor.get();
    }
  }

}
