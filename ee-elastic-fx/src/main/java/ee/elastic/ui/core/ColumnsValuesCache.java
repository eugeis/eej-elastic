package ee.elastic.ui.core;

import java.util.Map;

import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.integ.ParentChildValues;

@SuppressWarnings("rawtypes")
public class ColumnsValuesCache<V> {
  private ParentChildValues<Map, ColumnDef, V> cache = new ParentChildValues<>();

  public boolean exists(Map parent, ColumnDef child) {
    return cache.exists(parent, child);
  }

  public void clear() {
    cache.clear();
  }

  public V get(Map row, ColumnDef column) {
    return cache.get(row, column);
  }

  public void put(Map row, ColumnDef column, V value) {
    cache.put(row, column, value);
  }
}
