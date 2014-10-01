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

package org.elasticstore.common.validator;

import java.util.ArrayList;
import java.util.List;

import org.elasticstore.common.model.Item;

public abstract class ItemValidator {

    public static ItemValidationResult validate(Item item) {
        final List<String> errors = new ArrayList<>(4);
        if (item.getId() == null || item.getId().isEmpty()) {
            errors.add("Item id is empty");
        }
        if (item.getLabel() == null || item.getLabel().isEmpty()) {
            errors.add("Item label is empty");
        }
        return new ItemValidationResult(errors);
    }
}
