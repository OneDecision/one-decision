package link.omny.decisions.web;

import java.util.ArrayList;
import java.util.List;

import link.omny.decisions.model.ui.DecisionModel;
import link.omny.decisions.model.ui.ExampleModel;
import link.omny.decisions.model.ui.examples.ApplicationRiskRatingModel;
import link.omny.decisions.model.ui.examples.EmailFollowUpModel;
import link.omny.decisions.repositories.DecisionUIModelRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to support the decision definition UI.
 * 
 * <p>
 * For execution and interchange decision models are stored as DMN models.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/decision-ui-models")
public class DecisionUIModelController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DecisionUIModelController.class);

    @Autowired
    private DecisionUIModelRepository repo;

    private List<ExampleModel> examples;

    /**
     * Install example decision ui models.
     * 
     * @param tenantId
     */
    @RequestMapping(value = "/installExamples", method = RequestMethod.GET)
    public @ResponseBody void installExamples(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        for (ExampleModel ex : getExampleUIModels()) {
            createModelForTenant(tenantId, ex.getModel());
        }
    }

    private List<ExampleModel> getExampleUIModels() {
        if (examples == null) {
            examples = new ArrayList<ExampleModel>();
            examples.add(new ApplicationRiskRatingModel());
            examples.add(new EmailFollowUpModel());
        }
        return examples;
    }

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<DecisionModel> listForTenant(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("List decision models for tenant %1$s",
                tenantId));

        List<DecisionModel> list = repo.findAllForTenant(tenantId);
        LOGGER.info(String.format("Found %1$s decision ui models", list.size()));

        return list;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json" })
    public @ResponseBody DecisionModel getModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long id) {
        LOGGER.info(String.format(
                "Seeking decision model %1$s for tenant %2$s", id,
                tenantId));

        DecisionModel model = repo.findOneForTenant(tenantId, id);
        LOGGER.debug(String.format("... result from db: %1$s", model));

        if (model != null) {
            LOGGER.info("... found in repository");
        }
        return model;
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
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody DecisionModel createModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestBody DecisionModel model) {
        LOGGER.info(String.format("Creating decision model for tenant %1$s",
                tenantId));

        model.setTenantId(tenantId);
        // TODO perform checks that the model changes are not destructive.

        return repo.save(model);
    }

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param tenantId
     *            The tenant whose model is to be updated.
     * @param id
     *            Name of the decision to be updated.
     * @param model
     *            The updated model.
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody DecisionModel updateModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long id,
            @RequestBody DecisionModel model) {
        LOGGER.info(String.format(
                "Updating decision model %2$s for tenant %1$s", tenantId, id));

        if (!id.equals(model.getId())) {
            throw new IllegalStateException(
                    "Proposed model does not match the resource identifier");
        }
        // TODO perform checks that the model changes are not destructive.

        // model2 = repo.findById(tenantId, id);
        return repo.save(model);
    }

    /**
     * Delete the named model for the tenant.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     * @param id
     *            Name of a particular decision.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteModelForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long id) {
        LOGGER.info(String.format(
                "Deleting decision model %1$s for tenant %2$s", id,
                tenantId));

        repo.delete(id);
    }
}
