/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.elastic;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
* @author Eugen Eisler
*/
public class IndexAdmin implements Closeable {
	private static final String indexPropertiesFieldName = "value";
	private final String index;
	private final String indexPropertiesType;

	private ElasticAdmin admin;

	public IndexAdmin(String index, ElasticAdmin admin) {

		super();
		this.index = index;
		this.indexPropertiesType = index + "_properties_";
		this.admin = admin;
	}

	public void connect() {
		admin.connect();
	}

	@Override
	public void close() {
		admin.close();
	}

	public Object property(String id) {

		Object ret;
		GetRequestBuilder builder = prepareGet(this.indexPropertiesType, id);
		GetResponse response = builder.execute().actionGet();
		ret = response.isExists() ? response.getSource().get(
				indexPropertiesFieldName) : null;
		return ret;
	}

	public Date propertyAsDate(String id) {

		Date ret = null;
		Object dateAsObject = property(id);
		if (dateAsObject != null) {
			if (dateAsObject instanceof String) {
				ret = ISODateTimeFormat.dateOptionalTimeParser()
						.parseDateTime((String) dateAsObject).toDate();
			} else if (dateAsObject instanceof Date) {
				ret = (Date) dateAsObject;
			} else if (dateAsObject instanceof DateTime) {
				ret = ((DateTime) dateAsObject).toDate();
			}
		}
		return ret;
	}

	public void property(String id, Object value) throws IOException {

		XContentBuilder builder = jsonBuilder().startObject();
		builder.field(indexPropertiesFieldName, value);
		IndexRequestBuilder requestBuilder = this.prepareIndex(
				this.indexPropertiesType, id).setSource(builder.endObject());
		requestBuilder.execute().actionGet();
	}

	public boolean isConnect() {

		return admin.isConnect();
	}

	public Client getClient() {

		return admin.client();
	}

	public void deleteIndex() {
		admin.deleteIndex(index);
	}

	public void createIndex(Mapping[] mappingFilesInClassPath) {
		admin.checkIndex(index);
	}

	public void createMappings(Mapping[] mappingFilesInClassPath) {
		admin.createMappings(index, mappingFilesInClassPath);
	}

	public void recreateIndex(Mapping[] mappingFilesInClassPath) {
		admin.recreateIndex(index, mappingFilesInClassPath);
	}

	public void refeshIndex() {
		admin.refeshIndex(index);
	}

	public boolean checkIndex() {
		return admin.checkIndex(index);
	}

	public IndexRequestBuilder prepareIndex(String type, String id) {

		return getClient().prepareIndex(this.index, type, id);
	}

	public GetRequestBuilder prepareGet(String type, String id) {

		return getClient().prepareGet(this.index, type, id);
	}

	public SearchRequestBuilder prepareSearch() {

		return getClient().prepareSearch(this.index).setSearchType(
				SearchType.DFS_QUERY_THEN_FETCH);
	}

	public IndexRequestBuilder prepareIndex(String type) {

		return getClient().prepareIndex(this.index, type);
	}
}
