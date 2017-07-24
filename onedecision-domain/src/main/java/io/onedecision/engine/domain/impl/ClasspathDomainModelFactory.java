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
package io.onedecision.engine.domain.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainModel;

@Service
public class ClasspathDomainModelFactory implements DomainModelFactory {

    protected static ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ClasspathDomainModelFactory.class);

    @Value("${onedecision.domain.defaultDomainUri:http://onedecision.io/domains/cust-mgmt}")
    protected String defaultDomainUri;

    @Value("${onedecision.domain.defaultDomainResource:/domains/cust-mgmt.json}")
    protected String defaultDomainResource;

    protected Map<String, DomainModel> domains;

    public ClasspathDomainModelFactory() throws IOException {
        domains = new HashMap<String, DomainModel>();
    }

    public ClasspathDomainModelFactory(String key, String jsonResource)
            throws IOException {
        this();
        put(key, jsonResource);
    }

    public void put(String key, String jsonResource) throws IOException {
        domains.put(key, getJsonModel(jsonResource));
    }

    @Override
    public DomainModel fetchDomain(String domainModelUri) {
        /*if (domains.containsKey(domainModelUri)) {
            return domains.get(domainModelUri);
        } else */if (domainModelUri.equals(defaultDomainUri)) {
            LOGGER.warn(String
                    .format("Using default domain URI (%1$s) defined in classpath resource (%2$s). To override this replace the resource or inject your own DomainModelFactory",
                            defaultDomainUri, defaultDomainResource));
            try {
                put(defaultDomainUri, defaultDomainResource);
                return domains.get(defaultDomainUri);
            } catch (Exception e) {
                String msg = String
                        .format("The default domain URI (%1$s) was not found at the classpath resource %2$s as expected",
                                defaultDomainUri, defaultDomainResource);
                LOGGER.error(msg);
                throw new IllegalStateException(msg);
            }
        } else {
            throw new IllegalStateException(String.format(
                    "No domain model registered for URI: %1$s", domainModelUri));
        }
    }

    private DomainModel getJsonModel(String resource)
            throws JsonParseException, JsonMappingException, IOException {
        DomainModel jsonModel = mapper.readValue(getClass()
                .getResourceAsStream(resource), DomainModel.class);
        LOGGER.info("Loaded domain model from {}", resource);
        return jsonModel;
    }
}
