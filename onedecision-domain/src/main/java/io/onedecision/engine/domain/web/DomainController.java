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
package io.onedecision.engine.domain.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.model.DomainEntity;
import io.onedecision.engine.domain.model.DomainModel;
import io.onedecision.engine.domain.repositories.DomainModelRepository;

/**
 * Controller to access a tenant's domain model.
 * 
 * <p>
 * The domain model, sometimes called a data dictionary, for a tenant is the
 * common language and terminology for the application and specifically for the
 * decisions embedded.
 * 
 * @author Tim Stephenson
 *
 */
@Controller
@RequestMapping(value = "/{tenantId}/domain-model")
public class DomainController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DomainController.class);

    @Value("${onedecision.domain.defaultDomainUri:http://onedecision.io/domains/cust-mgmt}")
    protected String defaultDomainUri;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DomainModelRepository repo;

    @Autowired
    private DomainModelFactory domainModelFactory;

    /**
     * @param tenantId
     *            The tenant id.
     * @return The domain model for the specified tenant or a default customer
     *         management example if the tenant is not known.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody DomainModel getModelForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Seeking domain model for tenant %1$s",
                tenantId));

        DomainModel model = repo.findByName(tenantId);
        LOGGER.debug(String.format("... result: %1$s", model));

        if (model != null) {
            LOGGER.info("... found in repository");
            return model;
        } else {
            LOGGER.info("... not found, reliant on defaults");
            return domainModelFactory.fetchDomain(defaultDomainUri);
        }
    }

    /**
     * Imports JSON representation of contacts.
     * 
     * <p>
     * This is a handy link: http://shancarter.github.io/mr-data-converter/
     * 
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the JSON.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody DomainModel handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading domain model for: %1$s", tenantId));
        String content = new String(file.getBytes());

        DomainModel model = objectMapper.readValue(content,
                new TypeReference<DomainModel>() {
                });
        model.setTenantId(tenantId);

        LOGGER.info(String.format("  found model with %1$d entities", model
                .getEntities().size()));
        updateModelForTenant(tenantId, model);

        return model;
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param model
     *            The new domain model.
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public @ResponseBody DomainModel updateModelForTenant(
            @PathVariable("tenantId") String tenantId, @RequestBody DomainModel model) {
        LOGGER.info(String.format("Updating domain model for tenant %1$s",
                tenantId));
        model.setTenantId(tenantId);
        for (DomainEntity entity : model.getEntities()) {
            entity.setTenantId(tenantId);
        }

        // DomainModel existingModel = repo.findByName(tenantId);
        // if (existingModel == null) {
        // model.setRevision(1l);
        // } else {
        // // TODO perform checks that the model changes are not destructive.
        // model.setRevision(existingModel.getRevision() + 1);
        // }
        return repo.save(model);
    }

    /**
     * Update a single entity within the tenant's model.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param entityName
     *            Name of the entity being updated.
     */
    @RequestMapping(value = "/{entityName}", method = RequestMethod.PUT)
    public @ResponseBody void updateEntityForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("entityName") String entityName,
            @RequestBody DomainEntity entity) {
        LOGGER.info(String.format(
                "Updating domain model for tenant %1$s with entity %2$s",
                tenantId, entityName));
        entity.setTenantId(tenantId);
        DomainModel model = getModelForTenant(tenantId);
        int idx = -1;
        for (int i = 0; i < model.getEntities().size(); i++) {
            if (entityName.equals(model.getEntities().get(i).getName())) {
                idx = i;
            }
        }
        if (idx != -1) {
            model.getEntities().remove(idx);
        }
        model.getEntities().add(entity);

        updateModelForTenant(tenantId, model);
    }

    /**
     * Deleting a model does not remove any application data, just the
     * meta-data. This can be useful to reset a clean environment after testing
     * for example.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Deleting domain model for tenant %1$s",
                tenantId));

        repo.delete(repo.findByName(tenantId));
    }
}
