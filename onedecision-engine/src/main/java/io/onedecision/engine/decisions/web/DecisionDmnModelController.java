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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import io.onedecision.engine.decisions.api.RepositoryService;
import io.onedecision.engine.decisions.api.exceptions.DecisionNotFoundException;
import io.onedecision.engine.decisions.api.exceptions.NoDmnFileInUploadException;
import io.onedecision.engine.decisions.impl.DecisionModelFactory;
import io.onedecision.engine.decisions.impl.IdHelper;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.repositories.DecisionDmnModelRepository;

/**
 * Controller to manage native DMN decision definitions.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/decision-models")
@RepositoryRestResource(path = "/{tenantId}/decision-models")
public class DecisionDmnModelController extends DecisionModelFactory implements
        RepositoryService {
    public static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionDmnModelController.class);

    @Value("${onedecision.dmn.defaultStatus:Draft}" )
    private transient String defaultStatus = "Draft";

    @Autowired
    public DecisionDmnModelRepository repo;

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#listForTenant(java.lang.String)
     */
    @Override
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<DmnModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        List<DmnModel> list = repo.findAllForTenant(tenantId);
        LOGGER.info(String.format("Found %1$s decision models", list.size()));

        return addLinks(list);
    }

    @RequestMapping(value = "/{definitionOrInternalId}.html", method = RequestMethod.GET, produces = { "text/html" })
    public String getModelForTenantHtml(
            @PathVariable("definitionOrInternalId") String id,
            @PathVariable("tenantId") String tenantId, Model model) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id, tenantId));

        DmnModel dmnModel = null;
        try {
            dmnModel = getModelForTenant(Long.parseLong(id), tenantId);
        } catch (NumberFormatException e) {
            dmnModel = getModelForTenant(id, tenantId);
        } finally {
            model.addAttribute("dmnModel", dmnModel);
        }

        return "decisionModel";
    }

    @RequestMapping(value = "/{id}.html", method = RequestMethod.GET, produces = { "text/html" })
    @ResponseBody
    public String getDocumentationForTenant(@PathVariable("id") String id,
            @PathVariable("tenantId") String tenantId) {
        return super.getDocumentationForTenant(id, tenantId);
    }

    @RequestMapping(value = "/{definitionId}/{drgElementId}.html", method = RequestMethod.GET, produces = { "text/html" })
    public String getDrgElementForTenantHtml(
            @PathVariable("definitionId") String definitionId,
            @PathVariable("drgElementId") String drgElementId,
            @PathVariable("tenantId") String tenantId,
            @RequestParam(required = false) String edit,
            Model model) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s.%2$s for tenant %3$s",
                definitionId, drgElementId, tenantId));

        DmnModel dmnModel = getModelForTenant(definitionId, tenantId);
        Decision decision = dmnModel.getDefinitions().getDecision(drgElementId);
        if (decision == null || decision.getDecisionTable() == null) {
            // these are currently uneditable, render via XSL
            model.addAttribute("dmnModel", dmnModel);
            model.addAttribute("decision", decision);
            Map<String, String> params = new HashMap<String, String>();
            params.put("drgElementId", drgElementId);
            params.put("edit", edit == null ? "false" : "true");
            model.addAttribute(
                    "decisionHtml",
                    getTransformUtil().transform(dmnModel.getDefinitionXml(),
                            params));
            model.addAttribute("edit", edit == null ? false : true);

            return "decision";
        } else {
            // these ARE editable
            return "/decisions-table.html";
        }
    }

    @RequestMapping(value = "/{definitionId}/{decisionId}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DmnModel getDecisionServiceForTenant(
            @PathVariable("definitionId") String definitionId,
            @PathVariable("decisionId") String decisionId,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s.%2$s for tenant %3$s",
                definitionId, decisionId, tenantId));

        DmnModel dmnModel = getModelForTenant(definitionId, tenantId);
        Decision decision = dmnModel.getDefinitions().getDecision(decisionId);
        if (decision == null) {
            throw new DecisionNotFoundException(
                    String.format(
                    "Decision %1$s.%2$s not not exist for tenant %3$s",
                    definitionId, decisionId, tenantId));
        }
        // TODO should reduce this to only the requested or inferred decisions

        return addLinks(dmnModel);
    }

    /**
     * @param id Either definitions/@id of model or database id
     * @param tenantId
     * @return
     */
    @RequestMapping(value = "/{definitionOrInternalId}/", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DmnModel getModelForTenantRestApi(
            @PathVariable("definitionOrInternalId") String id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id, tenantId));

        try {
            return getModelForTenant(Long.parseLong(id), tenantId);
        } catch (NumberFormatException e) {
            return getModelForTenant(id, tenantId);
        }
    }

    @Override
    public @ResponseBody DmnModel getModelForTenant(
            @PathVariable("id") Long id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id, tenantId));

        DmnModel model = repo.findOneForTenant(id, tenantId);
        if (model == null) {
            throw new DecisionNotFoundException(tenantId, id.toString());
        }
        // indexModel(model);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return addLinks(model);
    }

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#getModelForTenant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public @ResponseBody DmnModel getModelForTenant(
            @PathVariable("definitionId") String definitionId,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", definitionId,
                tenantId));

        DmnModel model = repo.findByDefinitionId(definitionId, tenantId);
        if (model == null) {
            throw new DecisionNotFoundException(String.format(
                    "Decision model %1$s does not exist for tenant %2$s",
                    definitionId, tenantId));
        }
        // indexModel(model);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return addLinks(model);
    }

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#getDmnForTenant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{definitionOrInternalId}.dmn", method = RequestMethod.GET, produces = { "application/xml" })
    public @ResponseBody String getDmnForTenant(
            @PathVariable("definitionOrInternalId") String id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model (dmn) %1$s for tenant %2$s", id,
                tenantId));
        DmnModel model;
        try {
            model = repo.findOne(Long.parseLong(id));
        } catch (NumberFormatException e) {
            model = repo.findByDefinitionId(id, tenantId);
        }
        LOGGER.debug(String.format("... result from db: %1$s", model));
        return model.getDefinitionXml();
    }

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#getImageForTenant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{id}.dmn", method = RequestMethod.GET, produces = { "image/png" })
    public @ResponseBody byte[] getImageForTenant(
            @PathVariable("id") String id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Seeking decision model image %1$s for tenant %2$s", id,
                tenantId));

        DmnModel model = repo.findByDefinitionId(id, tenantId);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model.getImage();
    }

    /**
     * Upload DMN representation of decision.
     * 
     * @param files
     *            DMN files posted in a multi-part request and optionally an
     *            image of it.
     * @return The model created in the repository.
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
        if (deploymentMessage == null || deploymentMessage.length() == 0) {
            deploymentMessage = String.format("Deployed from file: %1$s",
                    dmnFileName);
        }
        DmnModel dmnModel = new DmnModel(dmnContent, deploymentMessage,
                image, tenantId);
        dmnModel.setName(IdHelper.toName(dmnFileName));
        dmnModel.setDefinitionXml(dmnContent);
        return createModelForTenant(dmnModel);
    }

    /**
     * Experimental.
     * 
     * @param model
     */
    protected void indexModel(DmnModel model) {
        model.setName(model.getDefinitions().getName());
        model.setDescription(model.getDefinitions().getDescription());
        // for (Decision d : model.getDefinitions().getDecisions()) {
        // model.getDecisionIds().add(d.getId());
        // model.getDecisionNames().add(d.getName());
        // }
        // for (BusinessKnowledgeModel bkm : model.getDefinitions()
        // .getBusinessKnowledgeModels()) {
        // model.getBusinessKnowledgeModelIds().add(bkm.getId());
        // model.getBusinessKnowledgeModelNames().add(bkm.getName());
        // }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody DmnModel createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody(required = false) DmnModel dmnModel,
            HttpServletRequest request, HttpServletResponse response,
            UriComponentsBuilder uriBuilder) {
        if (dmnModel == null) {
            dmnModel = DmnModel.newModel();
        }

        // ensure no discrepancy between tenant in URL and in model
        dmnModel.setTenantId(tenantId);
        dmnModel = createModelForTenant(dmnModel);
        // uriBuilder.fromPath();
        String url = request.getRequestURL().append(dmnModel.getShortId())
                .toString();
        response.setHeader("Location", url);
        dmnModel.addLink(new Link(url));
        return dmnModel;
    }

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#createModelForTenant(io.onedecision.engine.decisions.model.dmn.DmnModel)
     */
    @Override
    public DmnModel createModelForTenant(DmnModel model) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                model.getTenantId()));
        if (model.getStatus() == null) {
            model.setStatus(defaultStatus);
        }
        // indexModel(model);
        // TODO all decisions (and BKMs and ??) need to be given id if not
        // present in order to be retrieved later
        // TODO Also need to add version (default 1 and increment if present)
        model.setDefinitionXml(model.serialize(model.getDefinitions()));
        return repo.save(model);
    }

    /**
     * @see io.onedecision.engine.decisions.api.RepositoryService#updateModelForTenant(java.lang.String,
     *      io.onedecision.engine.decisions.model.dmn.DmnModel,
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

        model.setDefinitionXml(model.serialize(model.getDefinitions()));
        repo.save(model);
    }

    @Override
    public void deleteModelForTenant(Long id, String tenantId) {
        DmnModel model = getModelForTenant(id, tenantId);
        if (model == null) {
            throw new DecisionNotFoundException(String.format(
                    "Unable to find model for tenant %1$s with id %2$d",
                    tenantId, id));
        }
        repo.delete(model);
    }

    /**
     * @param id Either definitions/@id of model or database id
     * @see io.onedecision.engine.decisions.api.RepositoryService#deleteModelForTenant(java.lang.Long,
     *      java.lang.String)
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("id") String id,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", id, tenantId));

        DmnModel dmnModel = repo.findByDefinitionId(id, tenantId);
        if (dmnModel == null) {
            repo.delete(Long.valueOf(id));
        } else {
            repo.delete(dmnModel.getShortId());
        }
    }

    /** @deprecated Use {@link addLinks} */
    protected List<DmnModel> wrap(List<DmnModel> list) {
        return addLinks(list);
    }

    protected List<DmnModel> addLinks(List<DmnModel> list) {
        for (DmnModel model : list) {
            addLinks(model);
        }
        return list;
    }

    private DmnModel addLinks(DmnModel model) {
        model.addLink(new Link(getGlobalUri(model).toString(), Link.REL_SELF));
        return model;
    }

    protected URI getGlobalUri(DmnModel model) {
        try {
            UriComponentsBuilder builder = MvcUriComponentsBuilder
                    .fromController(getClass());
            String uri = builder.build().toUriString()
                    .replace("{tenantId}/", "");
            return new URI(String.format("%1$s/%2$d", uri, model.getShortId()));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }



}
