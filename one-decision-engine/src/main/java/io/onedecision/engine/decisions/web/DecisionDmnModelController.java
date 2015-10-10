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
package io.onedecision.engine.decisions.web;

import io.onedecision.engine.decisions.api.NoDmnFileInUploadException;
import io.onedecision.engine.decisions.api.RepositoryService;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.repositories.DecisionDmnModelRepository;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller to support the native DMN decision definition.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/decision-models")
public class DecisionDmnModelController extends DecisionModelFactory implements
        RepositoryService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionDmnModelController.class);

    @Autowired
    private DecisionDmnModelRepository repo;

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#listForTenant(java.lang.String)
     */
    @Override
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<DmnModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        List<DmnModel> list = repo.findAllForTenant(tenantId);
        LOGGER.info(String.format("Found %1$s decision models", list.size()));

        return list;
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#getModelForTenant(java.lang.String,
     *      java.lang.Long)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DmnModel getModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long id) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id, tenantId));

        DmnModel model = repo.findOneForTenant(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model;
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#getDmnForTenant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{id}.dmn", method = RequestMethod.GET, produces = { "application/xml" })
    public @ResponseBody String getDmnForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") String id) {
        LOGGER.info(String.format(
                "Seeking decision model (dmn) %1$s for tenant %2$s", id,
                tenantId));

        DmnModel model = repo.findByDefinitionId(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model.getDefinitionXml();
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#getImageForTenant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{id}.dmn", method = RequestMethod.GET, produces = { "image/png" })
    public @ResponseBody byte[] getImageForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") String id) {
        LOGGER.info(String.format(
                "Seeking decision model image %1$s for tenant %2$s", id,
                tenantId));

        DmnModel model = repo.findByDefinitionId(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model.getImage();
    }

    /**
     * Upload DMN representation of decision.
     * 
     * @param file
     *            A DMN file posted in a multi-part request and optionally an
     *            image of it.
     * @return
     * @throws IOException
     *             If cannot parse the file.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody DmnModel handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "deploymentMessage", required = false) String deploymentMessage,
            @RequestParam(value = "file", required = true) MultipartFile... files)
            throws IOException {
        LOGGER.info(String.format("Uploading dmn for: %1$s", tenantId));

        if (files.length > 2) {
            throw new IllegalArgumentException(
                    String.format(
                            "Expected one DMN file and optionally one image file but received %1$d",
                            files.length));
        }

        String dmnContent = null;
        String dmnFileName = null;
        byte[] image = null;
        for (MultipartFile resource : files) {
            LOGGER.debug(String.format("Deploying file: %1$s",
                    resource.getOriginalFilename()));
            if (resource.getOriginalFilename().toLowerCase().endsWith(".dmn")
                    || resource.getOriginalFilename().toLowerCase()
                            .endsWith(".dmn.xml")) {
                LOGGER.debug("... DMN resource");
                dmnContent = new String(resource.getBytes(), "UTF-8");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("DMN: " + dmnContent);
                }
                dmnFileName = resource.getOriginalFilename();
            } else {
                LOGGER.debug("... non-DMN resource");
                image = resource.getBytes();
            }
        }

        if (dmnContent == null) {
            throw new NoDmnFileInUploadException();
        }

        DmnModel dmnModel = createModelForTenant(load(dmnContent),
                deploymentMessage, image, tenantId);
        dmnModel.setName(dmnFileName);
        return dmnModel;
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#createModelForTenant(io.onedecision.engine.decisions.model.dmn.Definitions,
     *      java.lang.String, byte[],
     *      java.lang.String)
     */
    @Override
    public DmnModel createModelForTenant(Definitions model,
            String deploymentMessage, byte[] image,
            String tenantId) {
        DmnModel dmnModel = new DmnModel(model, deploymentMessage, image,
                tenantId);
        return createModelForTenant(dmnModel);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody DmnModel createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody DmnModel model) {
        // ensure no discrepancy between tenant in URL and in model
        model.setTenantId(tenantId);
        return createModelForTenant(model);
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#createModelForTenant(io.onedecision.engine.decisions.model.dmn.DmnModel)
     */
    @Override
    public DmnModel createModelForTenant(DmnModel model) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                model.getTenantId()));

        return repo.save(model);
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#updateModelForTenant(io.onedecision.engine.decisions.model.dmn.DmnModel,
     *      java.lang.String,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{definitionId}", method = RequestMethod.PUT)
    public @ResponseBody void updateModelForTenant(
            @PathVariable("definitionId") String definitionId,
            @RequestBody DmnModel model,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Updating decision model %2$s for tenant %1$s", tenantId,
                definitionId));

        if (!definitionId.equals(model.getDefinitionId())) {
            throw new IllegalStateException(
                    "Proposed model does not match the resource identifier");
        }
        // TODO perform checks that the model changes are not destructive.

        repo.save(model);
    }

    /**
     * @see io.onedecision.engine.decisions.web.RepositoryService#deleteModelForTenant(java.lang.String,
     *      java.lang.Long)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("id") Long id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", id, tenantId));

        repo.delete(id);
    }

}
