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
package org.elasticstore.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.elasticstore.common.model.Item;
import org.elasticstore.server.config.ElasticstoreConfiguration;
import org.elasticstore.server.integration.fixtures.Fixtures;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.junit.Test;

import javax.annotation.PostConstruct;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElasticstoreConfiguration.class)
@WebAppConfiguration
@ActiveProfiles("filesystem")
@IntegrationTest("server.port:8071")
public class ItemIT {

    @Value("${server.port}")
    protected int serverPort;
    protected String serverHost = "localhost";
    protected String serverUrl;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    private void init() {
        serverUrl = "http://" + serverHost + ":" + serverPort;
    }

    @Test
    public void testCreateRetrieveAndDeleteItem() throws Exception {
        final Item item = Fixtures.randomItem();
        // create the item
        HttpResponse resp = Request.Post(serverUrl + "/item")
                .bodyString(mapper.writeValueAsString(item), ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();

        assertEquals(201, resp.getStatusLine().getStatusCode());

        // retrieve the item
        resp = Request.Get(serverUrl + "/item/" + item.getId())
                .execute()
                .returnResponse();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        final Item fetched = mapper.readValue(resp.getEntity().getContent(), Item.class);
        assertEquals(item.getId(), fetched.getId());
        assertEquals(item.getLabel(), fetched.getLabel());
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getLastModified());

        // delete the item
        resp = Request.Delete(serverUrl + "/item/" + item.getId())
                .execute()
                .returnResponse();
        Assert.assertEquals(200, resp.getStatusLine().getStatusCode());

        //retrieve the item again and assert a 404
        resp = Request.Get(serverUrl + "/item/" + item.getId())
                .execute()
                .returnResponse();
        assertEquals(404, resp.getStatusLine().getStatusCode());
    }
}
