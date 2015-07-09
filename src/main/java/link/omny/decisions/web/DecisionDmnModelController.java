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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DmnModel getModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") String id) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id, tenantId));

        DmnModel model = repo.findByDefinitionId(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        return model;
    }

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

        return createModelForTenant(tenantId, dmnFileName, deploymentMessage,
                decisionModelFactory.load(dmnContent), image);
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
    public DmnModel createModelForTenant(String tenantId,
            String originalFileName, String deploymentMessage,
            Definitions model, byte[] image) {
        DmnModel dmnModel = createModelForTenant(tenantId, originalFileName,
                deploymentMessage, model);
        dmnModel.setImage(image);
        return dmnModel;
    }

    public DmnModel createModelForTenant(String tenantId,
            String originalFileName, String deploymentMessage, Definitions model) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                tenantId));

        // TODO perform checks that the model changes are not destructive.

        DmnModel dmnModel = converter.convert(model);
        dmnModel.setOriginalFileName(originalFileName);
        dmnModel.setDeploymentMessage(deploymentMessage);
        return createModelForTenant(tenantId, dmnModel);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody DmnModel createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody DmnModel model) {
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
     * @param id
     *            Id of a particular decision (allocated by the repository not
     *            any id from within the DMN).
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long id) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", id, tenantId));

        repo.delete(id);
    }
}
