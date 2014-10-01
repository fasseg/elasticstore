/* 
* Copyright 2014 Frank Asseg
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License. 
*/
package org.elasticstore.server.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class ElasticSearchNode {

    private Node node;

    @Value("${elasticsearch.cluster.name}")
    private String clusterName;
    @Value("${elasticsearch.path.logs}")
    private String logPath;
    @Value("${elasticsearch.path.data}")
    private String dataPath;
    @Value("${elasticsearch.bootstrap.mlockall}")
    private String mlockAll;
    @Value("${elasticsearch.network.bind.host}")
    private String bindHost;
    @Value("${elasticsearch.gateway.expected_nodes}")
    private String expectedNodes;
    @Value("${elasticsearch.http.port}")
    private String httpPort;
    @Value("${elasticsearch.http.enabled}")
    private String httpEnabled;
    @Value("${elasticsearch.transport.tcp.port}")
    private String tcpPort;
    @Value("${elasticsearch.network.publish_host}")
    private String publishHost;
    @Value("${elasticsearch.gateway.type}")
    private String gatewayType;


    @PostConstruct
    public void startNode() {
        node = NodeBuilder.nodeBuilder()
                        .clusterName(clusterName)
                        .settings(
                                ImmutableSettings.settingsBuilder()
                                        .put("path.logs",logPath)
                                        .put("path.data",dataPath)
                                        .put("bootstrap.mlockall",mlockAll)
                                        .put("network.bind.host",bindHost)
                                        .put("gateway.expected_nodes",expectedNodes)
                                        .put("http.port",httpPort)
                                        .put("http.enabled",httpEnabled)
                                        .put("transport.tcp.port",tcpPort)
                                        .put("network.publish_host",publishHost)
                                        .put("gateway.type",gatewayType)
                        )
                        .node();
        // wait for the node to be ready
        node.client()
                .admin()
                .cluster()
                .prepareHealth()
                .setWaitForYellowStatus()
                .execute()
                .actionGet();
    }

    @PreDestroy
    public void stopNode() {
        if (node != null) {
            node.stop();
        }
    }

    public Client getClient() {
        return node.client();
    }

    public boolean isAlive() {
        return node != null && !node.isClosed();
    }
}
