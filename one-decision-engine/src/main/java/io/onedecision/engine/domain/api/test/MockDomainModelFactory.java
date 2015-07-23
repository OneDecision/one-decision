/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.domain.api.test;

import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockDomainModelFactory implements DomainModelFactory {
    protected static ObjectMapper mapper = new ObjectMapper();

    protected Map<String, DomainModel> domains;

    public MockDomainModelFactory() {
        domains = new HashMap<String, DomainModel>();
    }

    public MockDomainModelFactory(String key, String jsonResource)
            throws JsonParseException, JsonMappingException, IOException {
        this();
        domains.put(key, getJsonModel(jsonResource));
    }

    @Override
    public DomainModel fetchDomain(String domainModelUri) {
        if (domains.containsKey(domainModelUri)) {
            return domains.get(domainModelUri);
        } else {
            throw new IllegalStateException(String.format(
                    "No domain model registered for URI: %1$s", domainModelUri));
        }
    }

    private DomainModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DomainModel jsonModel = mapper.readValue(getClass()
                .getResourceAsStream(resource), DomainModel.class);
        assertNotNull(jsonModel);
        return jsonModel;
    }
}
