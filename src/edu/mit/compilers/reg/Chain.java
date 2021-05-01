package edu.mit.compilers.reg;

import java.util.*;

public class Chain {

  private Chain parent;
  private Optional<Web> web;
  private Set<Chain> interference;

  public Chain() {
    parent = this;
    web = Optional.empty();
    interference = new HashSet<>();
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

}
