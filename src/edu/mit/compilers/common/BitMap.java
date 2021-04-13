package edu.mit.compilers.common;

import java.util.Map;
import java.util.HashMap;

public class BitMap<G> {

  private Map<G, Boolean> map = new HashMap<>();

  public BitMap() { }

  public BitMap(BitMap<G> other) {
    this.map = new HashMap<>(other.map);
  }

  public void set(G obj) {
    map.put(obj, true);
  }

  public void clear(G obj) {
    map.put(obj, false);
  }

  /**
   * @param obj
   * @return false if the object does not exist in the bitmap or is set to false;
   *  true otherwise
   */
  public boolean get(G obj) {
    return map.containsKey(obj) && map.get(obj);
  }

  public void and(BitMap<G> other) {
    for (G key : this.map.keySet()) {
      this.map.put(key, this.get(key) && other.get(key));
    }

    for (G key : other.map.keySet()) {
      this.map.put(key, this.get(key) && other.get(key));
    }
  }

  public void or(BitMap<G> other) {
    for (G key : other.map.keySet()) {
      this.map.put(key, this.get(key) || other.get(key));
    }
  }

  public void subsume(BitMap<G> other) {
    this.map = new HashMap<>(other.map);
  }

  public boolean sameValue(BitMap<G> other) {
    if (this.map.size() != other.map.size()) {
      return false;
    }

    for (G key : this.map.keySet()) {
      if (!other.map.containsKey(key)) {
        return false;
      }

      if (this.map.get(key) != other.map.get(key)) {
        return false;
      }
    }

    return true;
  }

}
