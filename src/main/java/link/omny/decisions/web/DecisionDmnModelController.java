package link.omny.decisions.web;

import java.io.IOException;
import java.util.List;

import link.omny.decisions.converter.DefinitionsDmnModelConverter;
import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.model.dmn.Definitions;
import link.omny.decisions.model.dmn.DmnModel;
import link.omny.decisions.repositories.DecisionDmnModelRepository;

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
public class DecisionDmnModelController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionDmnModelController.class);

    @Autowired
    private DecisionDmnModelRepository repo;

    @Autowired
    private DecisionModelFactory decisionModelFactory;

    @Autowired
    private DefinitionsDmnModelConverter converter;

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<DmnModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        List<DmnModel> list = repo.findAllForTenant(tenantId);
        LOGGER.info(String.format("Found %1$s decision models", list.size()));

        return list;
    }

    @RequestMapping(value = "/{definitionId}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DmnModel getModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("definitionId") String definitionId) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", definitionId,
                tenantId));


        DmnModel model = repo.findByDefinitionId(tenantId, definitionId);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model;
    }

    /**
     * Upload DMN representation of decision.
     * 
     * @param file
     *            A DMN file posted in a multi-part request
     * @throws IOException
     *             If cannot parse the file.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody void handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading activities for: %1$s", tenantId));
        String content = new String(file.getBytes());

        createModelForTenant(tenantId, decisionModelFactory.load(content));
    }

    public DmnModel createModelForTenant(String tenantId, Definitions model)
            throws IOException {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                tenantId));

        // TODO perform checks that the model changes are not destructive.

        return createModelForTenant(tenantId, converter.convert(model));
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant to create the model for.
     * @param model
     *            The new model.
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody DmnModel createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody DmnModel model) throws IOException {
        model.setTenantId(tenantId);
        return repo.save(model);
    }
    
    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param definitionId
     *            The id of the DMN root element.
     * @param model
     *            The updated model.
     */
    @RequestMapping(value = "/{definitionId}", method = RequestMethod.PUT)
    public @ResponseBody void updateModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("definitionId") String definitionId,
            @RequestBody DmnModel model) {
        LOGGER.info(String.format(
                "Updating decision model %2$s for tenant %1$s", tenantId,
                definitionId));

        if (!definitionId.equals(model.getDefinitionId())) {
            throw new IllegalStateException(
                    "Proposed model does not match the resource identifier");
        }
        // TODO perform checks that the model changes are not destructive.

        // model2 = repo.findById(tenantId, id);
        repo.save(model);
    }

    /**
     * Delete the specified model for the tenant.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     * @param definitionId
     *            Id of a particular decision.
     */
    @RequestMapping(value = "/{definitionId}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("definitionId") String definitionId) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", definitionId,
                tenantId));

        repo.delete(repo.findByDefinitionId(tenantId, definitionId));
    }
}
