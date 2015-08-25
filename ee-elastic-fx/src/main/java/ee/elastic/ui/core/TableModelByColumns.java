package ee.elastic.ui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValueBase;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.config.Metadata;
import ee.elastic.ui.integ.IndexTypeKeyValues;
import ee.elastic.ui.integ.IndexTypeValues;

public class TableModelByColumns extends ObservableValueBase<List<ColumnDef>> {
  private Metadata metadata;
  private SearchResponse searchResponse;
  private IndexTypeKeyValues<GetResponse> parents;
  private ArrayList<ColumnDef> previousColumns = new ArrayList<ColumnDef>();
  private ArrayList<ColumnDef> columns = new ArrayList<ColumnDef>();
  private HashSet<ColumnDef> distinctColumns = new HashSet<>();
  private HashSet<String> distinctColumnPaths = new HashSet<>();
  private ArrayList<Map<String, ?>> data;
  private IndexTypeValues<Boolean> handledColumns = new IndexTypeValues<>();

  public TableModelByColumns(Metadata metadata) {
    super();
    this.metadata = metadata;
    data = new ArrayList<>();
  }

  public void changeModelSource(SearchResponse searchResponse, IndexTypeKeyValues<GetResponse> parents) {
    this.searchResponse = searchResponse;
    this.parents = parents;
    buildModel();
  }

  public List<Map<String, ?>> getItems() {
    return data;
  }

  private void buildModel() {
    previousColumns = columns;
    columns = new ArrayList<>();
    data.clear();
    distinctColumns.clear();
    handledColumns.clear();
    distinctColumnPaths.clear();
    for (SearchHit hit : searchResponse.getHits().getHits()) {
      data.add(buildRow(hit));
    }
    fireValueChangedEvent();
  }

  @Override
  public List<ColumnDef> getValue() {
    return columns;
  }

  public List<ColumnDef> getColumns() {
    return columns;
  }

  public boolean isColumnChanged() {
    return !previousColumns.equals(columns);
  }

  private HashMap<String, Object> buildRow(SearchHit hit) {
    HashMap<String, Object> ret = buildSource(hit);
    if (handledColumns.get(hit.index(), hit.type()) == null) {
      handledColumns.put(hit.index(), hit.type(), Boolean.TRUE);
      List<ColumnDef> columns = metadata.browserColumns(hit.index(), hit.type());
      if (columns != null) {
        for (ColumnDef column : columns) {
          checkAndAddColumn(column);
        }
      } else {
        createColumnsRecursive("", ret);
      }
    }
    return ret;
  }

  private HashMap<String, Object> buildSource(SearchHit hit) {
    HashMap<String, Object> source = new HashMap<>();
    source.put("_id", hit.id());
    source.put("_index", hit.index());
    source.put("_type", hit.type());
    source.put("_source", hit.sourceAsMap());
    HashMap<Object, Object> fields = new HashMap<>();
    source.put("fields", fields);
    for (SearchHitField hitField : hit.fields().values()) {
      fields.put(hitField.name(), hitField.value());
    }
    source.put("_version", hit.getVersion());
    GetResponse parent = getParent(hit);
    HashMap<Object, Object> _parent = new HashMap<>();
    source.put("_parent", _parent);
    if (parent != null) {
      _parent.put("_source", parent.getSource());
    }
    return source;
  }

  private GetResponse getParent(SearchHit hit) {
    GetResponse parent = null;
    if (parents != null) {
      String parentType = metadata.parentType(hit.index(), hit.type());
      parent = parents.get(hit.index(), parentType, parentField(hit));
    }
    return parent;
  }

  private String parentField(SearchHit hit) {
    String ret = null;
    if (hit.getFields() != null) {
      SearchHitField _parent = hit.getFields().get("_parent");
      if (_parent != null) {
        ret = _parent.getValue();
      }
    }
    return ret;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void createColumnsRecursive(String parentPath, Map<String, ? extends Object> spec) {
    for (Map.Entry<String, ? extends Object> prop : spec.entrySet()) {
      String path = parentPath;
      if (!path.isEmpty()) {
        path = path.concat(".");
      }
      path = path.concat(prop.getKey());
      if (prop.getValue() instanceof Map) {
        createColumnsRecursive(path, Map.class.cast(prop.getValue()));
      } else if (prop.getValue() instanceof SearchHitField) {
        SearchHitField searchHitField = (SearchHitField) prop.getValue();
        if (searchHitField.getValue() instanceof Map) {
          createColumnsRecursive(path, ((Map) searchHitField.getValue()));
        } else {
          checkAndCreateColumn(path);
        }
      } else {
        checkAndCreateColumn(path);
      }
    }
  }

  private void checkAndCreateColumn(String path) {
    if (!distinctColumnPaths.contains(path)) {
      distinctColumnPaths.add(path);
      String label = path.replaceAll("(_source|fields)\\.", "");
      checkAndAddColumn(new ColumnDef(path, label));
    }
  }

  private void checkAndAddColumn(ColumnDef column) {
    if (!distinctColumns.contains(column)) {
      distinctColumns.add(column);
      columns.add(column);
    }
  }
}
