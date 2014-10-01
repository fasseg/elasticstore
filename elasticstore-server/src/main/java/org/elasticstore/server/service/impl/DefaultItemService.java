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

import org.elasticstore.common.model.Item;
import org.elasticstore.common.validator.ItemValidationResult;
import org.elasticstore.common.validator.ItemValidator;
import org.elasticstore.server.ElasticStoreException;
import org.elasticstore.server.ValidationException;
import org.elasticstore.server.service.BlobstoreService;
import org.elasticstore.server.service.ElasticSearchService;
import org.elasticstore.server.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultItemService implements ItemService {

    @Autowired
    private BlobstoreService blobstoreService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Override
    public void saveOrUpdateItem(Item item) throws ElasticStoreException {

        // validate the item
        final ItemValidationResult validation = ItemValidator.validate(item);
        if (!validation.isSuccessful()) {
            throw new ValidationException(validation.getErrors());
        }

        // write the item to elastic search
        elasticSearchService.saveOrUpdateItem(item);
    }

    @Override
    public void delete(String itemId) throws ElasticStoreException {
        elasticSearchService.deleteItem(itemId);
    }

    @Override
    public Item retrieve(String itemId) throws ElasticStoreException {
        return elasticSearchService.retrieveItem(itemId);
    }

}
