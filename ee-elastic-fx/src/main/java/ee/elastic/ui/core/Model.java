package ee.elastic.ui.core;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

import ee.elastic.ElasticAdmin;
import ee.elastic.NodeType;
import ee.elastic.ui.config.Field;
import ee.elastic.ui.config.Metadata;
import ee.elastic.ui.integ.IndexTypeKeyValues;
import ee.elastic.ui.integ.Link;

public class Model {
	private ElasticAdmin elasticSearchClient;
	private final Metadata metadata;
	private ParentLoader parentLoader;
	private HashSet<String> searchIndices;
	private HashSet<String> searchTypes;
	private HashMap<Field, Object> searchFields;
	private List<Sort<String, SortOrder>> sortDefs;
	private int rowCount = 50;

	public Model() {
		metadata = new Metadata();
	}

	public void connect(URL url) {
		close();

		elasticSearchClient = new ElasticAdmin(NodeType.Transport);
		elasticSearchClient.connect();
		elasticSearchClient.addTransportAddress(url);
		initMetadata();
		parentLoader = new ParentLoader(metadata, elasticSearchClient);
		searchIndices = new HashSet<>();
		searchTypes = new HashSet<>();
		searchFields = new HashMap<>();
	}

	private void initMetadata() {
		metadata.init(elasticSearchClient.state(), elasticSearchClient.status(), elasticSearchClient.stats());
	}

	public SearchResponse search() {
		System.out.println();
		SearchRequestBuilder requestBuilder = elasticSearchClient.client()
				.prepareSearch(searchIndices.toArray(new String[0])).setTypes(searchTypes.toArray(new String[0]))
				.addFields("_source", "_parent").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0)
				.setSize(rowCount).setExplain(false);

		if (!searchFields.isEmpty()) {
			BoolQueryBuilder query = boolQuery();
			for (Entry<Field, Object> searchEntry : searchFields.entrySet()) {
				if (searchEntry.getValue() instanceof Link) {
					Link<?> range = (Link<?>) searchEntry.getValue();
					query.must(rangeQuery(searchEntry.getKey().name()).from(range.from()).to(range.to()));
				} else {
					query.must(wildcardQuery(searchEntry.getKey().name(), searchEntry.getValue().toString()));
				}
			}
			requestBuilder.setQuery(query);
		}
		if (sortDefs != null) {
			for (Sort<String, SortOrder> sort : sortDefs) {
				requestBuilder.addSort(sort.element(), sort.order());
			}
		}
		System.out.println("Search: " + requestBuilder.toString());

		SearchResponse response = requestBuilder.execute().actionGet();
		return response;
	}

	public Metadata metadata() {
		return metadata;
	}

	public IndexTypeKeyValues<GetResponse> loadParents(SearchResponse response) {
		return parentLoader.loadParents(response);
	}

	public void searchIndex(String index, Boolean enabled) {
		if (enabled) {
			searchIndices.add(index);
		} else {
			searchIndices.remove(index);
		}
	}

	public HashSet<String> getSearchIndices() {
		return searchIndices;
	}

	public HashSet<String> getSearchTypes() {
		return searchTypes;
	}

	public void searchType(String type, Boolean enabled) {
		if (enabled) {
			searchTypes.add(type);
		} else {
			searchTypes.remove(type);
		}
	}

	public void searchWildcard(Field field, String searchValue) {
		if (searchValue == null || searchValue.isEmpty()) {
			searchFields.remove(field);
		} else {
			searchFields.put(field, searchValue);
		}
	}

	public void close() {
		if (elasticSearchClient != null) {
			elasticSearchClient.close();
		}
	}

	public String connectionStatus() {
		return isConnected() ? elasticSearchClient.healthStatus().toString() : "not connected";
	}

	public boolean isConnected() {
		return elasticSearchClient != null && elasticSearchClient.isConnect();
	}

	public void clearSearch() {
		searchIndices.clear();
		searchTypes.clear();
		searchFields.clear();
	}

	public void sortOrder(List<Sort<String, SortOrder>> sortDefs) {
		this.sortDefs = sortDefs;
	}

	public void rowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int rowCount() {
		return rowCount;
	}

	public void updateMapping(String index, String type, String mapping) {
		elasticSearchClient.putMapping(index, type, mapping);
	}

	public void updateMapping(String index, String type, Map<?, ?> mapping) {
		elasticSearchClient.putMapping(index, type, mapping);
		initMetadata();
	}

	public void clearSearchIndices() {
		searchIndices.clear();
	}

	public void clearSearchTypes() {
		searchTypes.clear();
	}

	public void clearSearchFields() {
		searchFields.clear();
	}

	public void refreshMetadata() {
		initMetadata();
	}

	public void deleteIndex(String selectedIndex) {
		elasticSearchClient.deleteIndex(selectedIndex);
		initMetadata();
	}
}
