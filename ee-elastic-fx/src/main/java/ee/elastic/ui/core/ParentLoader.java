package ee.elastic.ui.core;

import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest.Item;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import ee.elastic.ElasticAdmin;
import ee.elastic.ui.config.Metadata;
import ee.elastic.ui.integ.IndexTypeKeyValues;

public class ParentLoader {
  private Metadata metadata;
  private ElasticAdmin elasticSearchAdmin;

  public ParentLoader(Metadata metadata, ElasticAdmin elasticSearchAdmin) {
    super();
    this.metadata = metadata;
    this.elasticSearchAdmin = elasticSearchAdmin;
  }

  private String getParentField(SearchHit hit) {
    String ret = null;
    if (hit.getFields() != null) {
      SearchHitField _parent = hit.getFields().get("_parent");
      ret = _parent.getValue();
    }
    return ret;
  }

  private List<Item> buildParentItems(SearchResponse response) {
    IndexTypeKeyValues<Item> indexToTypeToParentIds = new IndexTypeKeyValues<>();
    for (SearchHit hit : response.getHits()) {
      if (metadata.isLoadParents(hit.getIndex(), hit.getType())) {
        String parent = getParentField(hit);
        if (parent != null) {
          String parentType = metadata.parentType(hit.getIndex(), hit.getType());
          if (!indexToTypeToParentIds.exists(hit.getIndex(), parentType, parent)) {
            indexToTypeToParentIds.put(hit.getIndex(), hit.getType(), parent, new Item(hit.getIndex(), parentType, parent));
          }
        }
      }
    }
    return indexToTypeToParentIds.keyValues();
  }

  public IndexTypeKeyValues<GetResponse> loadParents(SearchResponse response) {
    IndexTypeKeyValues<GetResponse> ret = null;
    List<Item> items = buildParentItems(response);
    if (!items.isEmpty()) {
      MultiGetRequestBuilder prepareMultiGet = elasticSearchAdmin.client().prepareMultiGet();
      for (Item item : items) {
        prepareMultiGet.add(item);
      }
      MultiGetResponse multiGetResponse = prepareMultiGet.execute().actionGet();
      ret = buildMapByIndexTypeIdDoc(multiGetResponse);
    }
    return ret;
  }

  private IndexTypeKeyValues<GetResponse> buildMapByIndexTypeIdDoc(MultiGetResponse multiGetResponse) {
    IndexTypeKeyValues<GetResponse> ret = new IndexTypeKeyValues<>();
    for (MultiGetItemResponse response : multiGetResponse) {
      ret.put(response.getIndex(), response.getType(), response.getResponse().getId(), response.getResponse());
    }
    return ret;
  }
}
