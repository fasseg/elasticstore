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

import java.io.IOException;
import java.io.InputStream;

import org.elasticstore.server.service.BlobstoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("filesystem")
public class PosixBlobStoreService implements BlobstoreService {

    @Value("${filesystem.data.path}")
    private String storagePath;


    @Override
    public void saveOrUpdate(String path, InputStream src) throws IOException {

    }

    @Override
    public void delete(String path) throws IOException {

    }

    @Override
    public void exists(String path) throws IOException {

    }

    @Override
    public InputStream openStream(String path) throws IOException {
        return null;
    }
}
