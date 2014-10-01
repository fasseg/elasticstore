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

package org.elasticstore.server.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticstore.common.model.Item;
import org.elasticstore.server.ElasticStoreException;
import org.elasticstore.server.ItemNotFoundException;
import org.elasticstore.server.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchServiceImpl implements ElasticSearchService {

    public static final String INDEX_ITEMS = "items";

    public static final String INDEX_ITEM_TYPE = "item";

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void init() throws IOException {
        this.checkAndOrCreateIndex(INDEX_ITEMS);
        this.waitForIndex(INDEX_ITEMS);
    }

    protected void waitForIndex(String indexName) {
            this.client.admin()
                    .cluster()
                    .prepareHealth(indexName)
                    .setWaitForYellowStatus()
                    .execute()
                    .actionGet();
    }

    protected void checkAndOrCreateIndex(String indexName){
            if (!indexExists(indexName)) {
                this.client.admin()
                        .indices()
                        .prepareCreate(indexName)
                        .execute()
                        .actionGet();
            }
    }

    protected boolean indexExists(String indexName) {
            return this.client.admin()
                        .indices()
                        .exists(new IndicesExistsRequest(indexName))
                        .actionGet()
                        .isExists();
    }

    protected void refreshIndex(String... indices) {
            client.admin()
                    .indices()
                    .refresh(new RefreshRequest(indices))
                    .actionGet();
    }

    @Override
    public Item retrieveItem(String itemId) throws ElasticStoreException {
        final GetResponse resp = this.client.prepareGet(INDEX_ITEMS, INDEX_ITEM_TYPE, itemId)
                .execute()
                .actionGet();
        try {
            if (!resp.isExists()) {
                throw new ItemNotFoundException(itemId);
            }
            return mapper.readValue(resp.getSourceAsBytes(), Item.class);
        }catch (IOException e) {
            throw new ElasticStoreException(e);
        }
    }

    @Override
    public void saveOrUpdateItem(Item item) throws ElasticStoreException {
        log.debug("indexing item " + item.getId());
        // set created date for new items
        if (!exists(item.getId())) {
            item.setCreated(Instant.now());
        }
        // set the last modifed date
        item.setLastModified(Instant.now());

        // store the item in ElasticSearch
        try {
            final IndexResponse resp = client.prepareIndex(INDEX_ITEMS, INDEX_ITEM_TYPE, item.getId())
                    .setSource(mapper.writeValueAsBytes(item))
                    .execute()
                    .actionGet();
        }catch (IOException e) {
            throw new ElasticStoreException(e);
        }
        refreshIndex(INDEX_ITEMS);
    }

    @Override
    public void deleteItem(String itemId) throws ElasticStoreException {
        this.client.prepareDelete(INDEX_ITEMS, INDEX_ITEM_TYPE, itemId)
                .execute()
                .actionGet();
    }

    @Override
    public boolean exists(String itemId) throws ElasticStoreException {
        return this.client.prepareGet(INDEX_ITEMS, INDEX_ITEM_TYPE, itemId)
                .execute()
                .actionGet()
                .isExists();
    }
}
