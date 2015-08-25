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

import static org.elasticsearch.client.Requests.*;
import static org.elasticsearch.common.io.Streams.*;
import static org.elasticsearch.node.NodeBuilder.*;

import java.awt.geom.IllegalPathStateException;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

/**
 * @author Eugen Eisler
 */
public class ElasticAdmin implements Closeable {
  public static final String ADDRESSES = "addresses";

  private Node node;
  private Client client;
  private NodeType nodeType;
  private Map<String, Integer> hostToPort;

  public ElasticAdmin(NodeType nodeType) {

    super();
    this.nodeType = nodeType;
    hostToPort = new HashMap<>();
    fillAddresses();
  }

  public ElasticAdmin() {
    this(NodeType.Transport);
  }

  public void connect() {
    if (NodeType.Local == nodeType) {
      node = nodeBuilder().local(true).node();
      client = node.client();
    } else if (NodeType.Client == nodeType) {
      node = nodeBuilder()
          .settings(
              ImmutableSettings.settingsBuilder().put("http.enabled", false))
          .client(true).node();
      client = node.client();
    } else if (NodeType.Transport == nodeType) {
      client = new TransportClient();
    }
    bindAddresses();
  }

  protected void bindAddresses() {
    for (Entry<String, Integer> hostPort : hostToPort.entrySet()) {
      bindAddress(hostPort.getKey(), hostPort.getValue());
    }
  }

  private void fillAddresses() {
    String addresses = System.getProperty(ADDRESSES, null);
    if (addresses != null) {
      for (String address : addresses.split(",")) {
        String[] hostAndPort = address.split(":");
        addServerAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
      }
    }
  }

  public void addTransportAddress(URL url) {
    addServerAddress(url.getHost(), url.getPort());
  }

  public void addServerAddress(String host, int port) {
    hostToPort.put(host, port);
    bindAddress(host, port);
  }

  protected void bindAddress(String host, int port) {
    if (client instanceof TransportClient) {
      InetSocketTransportAddress address = new InetSocketTransportAddress(host,
          port);
      TransportClient transportClient = (TransportClient) client;
      try {
        transportClient.addTransportAddress(address);
      } catch (RuntimeException runtimeException) {
        transportClient.removeTransportAddress(address);
        throw runtimeException;
      } catch (Exception exception) {
        transportClient.removeTransportAddress(address);
        throw new RuntimeException(exception);
      }
    }
  }

  @Override
  public void close() {

    if (isConnect()) {
      if (client != null) {
        client.close();
      }

      if (node != null) {
        node.close();
        node = null;
      }

    }
  }

  public boolean isConnect() {

    return client != null;
  }

  public ClusterHealthStatus healthStatus() {
    ClusterHealthStatus ret = ClusterHealthStatus.RED;
    if (isConnect()) {
      ClusterHealthResponse response = client.admin().cluster()
          .health(new ClusterHealthRequest()).actionGet();
      ret = response.getStatus();
    }
    return ret;
  }

  public Client client() {

    return client;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public ClusterState state() {
    ClusterStateResponse ret = client.admin().cluster()
        .state(new ClusterStateRequest()).actionGet();
    return ret.getState();
  }

  public Map<String, IndexStatus> status() {
    IndicesStatusResponse ret = client.admin().indices()
        .status(new IndicesStatusRequest()).actionGet();
    return ret.getIndices();
  }

  public Map<String, IndexStats> stats() {
    IndicesStatsResponse ret = client.admin().indices()
        .stats(new IndicesStatsRequest()).actionGet();
    return ret.getIndices();
  }

  public void createMappings(String index, Mapping[] mappingFilesInClassPath) {
    try {
      if (mappingFilesInClassPath != null) {
        for (Mapping mappingFileInClassPath : mappingFilesInClassPath) {
          String mapping = copyToStringFromClasspath(mappingFileInClassPath
              .getFile());
          String type = mappingFileInClassPath.getName();
          putMapping(index, type, mapping);
        }
      }
    } catch (IOException e) {
      throw new IllegalPathStateException(e.getMessage());
    }
  }

  public void putMapping(String index, String type, String mapping) {
    PutMappingRequest putMappingRequest = putMappingRequest(index).type(type)
        .source(mapping);
    PutMappingResponse response = client.admin().indices()
        .putMapping(putMappingRequest).actionGet();
    System.out.println("Mapping updated? = " + response.isAcknowledged());
  }

  public void putMapping(String index, String type, Map<?, ?> mapping) {
    PutMappingRequest putMappingRequest = putMappingRequest(index).type(type)
        .source(mapping);
    PutMappingResponse response = client.admin().indices()
        .putMapping(putMappingRequest).actionGet();
    System.out.println("Mapping updated? = " + response.isAcknowledged());
  }

  public void createIndex(String index, Mapping[] mappingFilesInClassPath) {

    @SuppressWarnings("unused")
    CreateIndexResponse response = client.admin().indices()
        .create(createIndexRequest(index)).actionGet();
    if (checkIndex(index)) {
      createMappings(index, mappingFilesInClassPath);
    } else {
      deleteIndex(index);
      throw new IllegalStateException("Index don't exist");
    }
  }

  public void deleteIndex(String index) {

    try {
      client.admin().indices().delete(deleteIndexRequest(index)).actionGet();
    } catch (Exception e) {
      // nothing
    }
  }

  public void recreateIndex(String index, Mapping[] mappingFilesInClassPath) {

    deleteIndex(index);
    createIndex(index, mappingFilesInClassPath);
  }

  public void refeshIndex(String index) {

    client.admin().indices().refresh(refreshRequest(index)).actionGet();
  }

  public boolean checkIndex(String index) {

    ClusterHealthResponse response = client.admin().cluster()
        .health(new ClusterHealthRequest(index).waitForYellowStatus())
        .actionGet();
    return response.getIndices().containsKey(index);
  }
}
