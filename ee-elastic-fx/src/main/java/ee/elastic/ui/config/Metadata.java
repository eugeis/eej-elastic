package ee.elastic.ui.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;

import ee.elastic.ui.integ.IndexTypeKeyValues;
import ee.elastic.ui.integ.IndexTypeValues;
import ee.elastic.ui.integ.NameValues;

public class Metadata {
  private IndexTypeValues<TypeMapping> typeMappings;
  private IndexTypeKeyValues<Field> typeFields;
  private ClusterState state;
  private NameValues<String> indexTypes;
  private NameValues<Field> indexFields;
  private Map<String, IndexStatus> status;
  private Map<String, IndexStats> stats;

  public Metadata() {
    super();
  }

  public Metadata(ClusterState state, Map<String, IndexStatus> status, Map<String, IndexStats> stats) {
    super();
    init(state, status, stats);
  }

  public boolean isLoadParents(String index, String type) {
    VisualConfig config = visualConfig(index, type);
    return config != null && config.isLoadParents() != null && config.isLoadParents();
  }

  public VisualConfig visualConfig(String index, String type) {
    return typeMappings.get(index, type).visualConfig();
  }

  public List<ColumnDef> browserColumns(String index, String type) {
    List<ColumnDef> ret = null;
    VisualConfig visualConfig = visualConfig(index, type);
    if (visualConfig != null) {
      BrowserDef browser = visualConfig.browser();
      if (browser != null) {
        ret = browser.columns();
      }
    }
    return ret;
  }

  public ViewDef view(String index, String type) {
    ViewDef ret = null;
    VisualConfig visualConfig = visualConfig(index, type);
    if (visualConfig != null) {
      ret = visualConfig.detailsView();
    }
    return ret;
  }

  public String parentType(String index, String type) {
    return typeMappings.get(index, type).parentType();
  }

  public Set<String> indices() {
    return indexTypes.names();
  }

  public Set<String> types() {
    return indexTypes.values();
  }

  public Set<Field> fields() {
    return indexFields.values();
  }

  @SuppressWarnings("unchecked")
  public void init(ClusterState state, Map<String, IndexStatus> status, Map<String, IndexStats> stats) {
    this.state = state;
    this.status = status;
    this.stats = stats;

    typeMappings = new IndexTypeValues<>();
    typeFields = new IndexTypeKeyValues<>();
    indexTypes = new NameValues<>();
    indexFields = new NameValues<>();
    for (ObjectObjectCursor<String, IndexMetaData> indexMetaDataCursor : state.getMetaData().getIndices()) {
      IndexMetaData indexMetaData = indexMetaDataCursor.value;
      for (ObjectObjectCursor<String, MappingMetaData> mappingMetaDataCursor : indexMetaData.getMappings()) {
        MappingMetaData mappingMetaData = mappingMetaDataCursor.value;
        indexTypes.add(indexMetaData.getIndex(), mappingMetaData.type());
        try {
          Map<String, Object> sourceAsMap = mappingMetaData.getSourceAsMap();
          for (Map.Entry<String, Map<String, ?>> field : ((Map<String, Map<String, ?>>) sourceAsMap.get("properties")).entrySet()) {
            Field typeField = new Field(field.getKey(), String.valueOf(field.getValue().get("type")));
            indexFields.add(indexMetaData.getIndex(), typeField);
            typeFields.put(indexMetaData.getIndex(), mappingMetaData.type(), field.getKey(), typeField);
          }
          typeMappings.put(indexMetaData.index(), mappingMetaData.type(), new TypeMapping(mappingMetaData.type(), sourceAsMap));

          // add _all field
          Field typeField = new Field("_all", "String");
          indexFields.add(indexMetaData.getIndex(), typeField);
          typeFields.put(indexMetaData.getIndex(), mappingMetaData.type(), "_all", typeField);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public IndexMetaData metadata(String index) {
    return state.metaData().index(index);
  }

  public MappingMetaData metadata(String index, String type) {
    return metadata(index).mappings().get(type);
  }

  public Set<String> types(Set<String> indices) {
    return subSet(indexTypes, indices);
  }

  public Set<String> types(String index) {
    return indexTypes.get(index);
  }

  private <E> Set<E> subSet(NameValues<E> source, Set<String> subKeys) {
    if (!subKeys.isEmpty()) {
      if (subKeys.size() > 1) {
        HashSet<E> ret = new HashSet<>();
        for (String key : subKeys) {
          ret.addAll(source.get(key));
        }
        return ret;
      } else {
        return source.get(subKeys.iterator().next());
      }
    } else {
      return source.values();
    }
  }

  public IndexStatus status(String index) {
    return status.get(index);
  }

  public ClusterState state() {
    return state;
  }

  public Map<String, IndexStatus> status() {
    return status;
  }

  public Map<String, IndexStats> stats() {
    return stats;
  }

  public IndexStats stats(String index) {
    return stats.get(index);
  }

  public Set<Field> fields(Set<String> indices, Set<String> types) {
    HashMap<String, Field> ret = new HashMap<>();
    for (String index : indices.isEmpty() ? typeFields.parentKeys() : indices) {
      for (String type : types.isEmpty() ? typeFields.childKeys(index) : types) {
        Map<String, Field> fields = typeFields.get(index, type);
        if (fields != null) {
          ret.putAll(fields);
        }
      }
    }
    return new HashSet<Field>(ret.values());
  }
}
