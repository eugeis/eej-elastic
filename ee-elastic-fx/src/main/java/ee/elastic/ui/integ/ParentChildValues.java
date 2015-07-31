package ee.elastic.ui.integ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ParentChildValues<P, C, V> {
  private HashMap<P, HashMap<C, V>> parentToChildMap = new HashMap<>();

  public boolean exists(P parent, C child) {
    return get(parent, child) != null;
  }

  public V get(P parent, C child) {
    V ret = null;
    HashMap<C, V> parentMap = parentToChildMap.get(parent);
    if (parentMap != null) {
      ret = parentMap.get(child);
    }
    return ret;
  }

  public Object put(P parent, C child, V value) {
    HashMap<C, V> childMap = parentToChildMap.get(parent);
    if (childMap == null) {
      childMap = new HashMap<>();
      parentToChildMap.put(parent, childMap);
    }
    return childMap.put(child, value);
  }

  public List<V> values() {
    ArrayList<V> ret = new ArrayList<>();
    for (HashMap<C, V> childMap : parentToChildMap.values()) {
      ret.addAll(childMap.values());
    }
    return ret;
  }

  public Set<P> parentKeys() {
    return parentToChildMap.keySet();
  }

  public Set<C> childKeys(P parent) {
    return parentToChildMap.get(parent).keySet();
  }

  public void clear() {
    parentToChildMap.clear();
  }
}
