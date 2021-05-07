package edu.mit.compilers.reg;

import edu.mit.compilers.common.Triple;

import java.util.*;

public class Chain {

  private Triple id;
  private Chain parent;
  private Optional<Web> web;
  private Set<Chain> interference;
  private final Optional<String> precolor;

  public Chain(final Triple id) {
    this.id = id;
    parent = this;
    web = Optional.empty();
    interference = new HashSet<>();
    precolor = Optional.empty();
  }

  public Chain(final Triple id, final String precolor) {
    this.id = id;
    parent = this;
    web = Optional.empty();
    interference = new HashSet<>();
    this.precolor = Optional.of(precolor);
  }

  public String getIndex() {
    return id.toString();
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

  @Override
  public boolean equals(Object that) {
    return that instanceof Chain && sameValue((Chain) that);
  }

  private boolean sameValue(Chain that) {
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}
