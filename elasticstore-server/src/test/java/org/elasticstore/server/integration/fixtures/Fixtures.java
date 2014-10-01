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
package org.elasticstore.server.integration.fixtures;

import org.apache.commons.lang.RandomStringUtils;
import org.elasticstore.common.model.Collection;
import org.elasticstore.common.model.Item;

import java.time.ZonedDateTime;

public abstract class Fixtures {
    public static Item randomItem() {
        Item i = new Item();
        i.setCollection(randomCollection());
        i.setLabel(randomName());
        i.setId(randomName());
        return i;
    }

    public static Collection randomCollection() {
        Collection coll = new Collection();
        coll.setName(randomName());
        return coll;
    }

    public static String randomName() {
        return RandomStringUtils.randomAlphabetic(16);
    }
}
