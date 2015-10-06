package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.util.List;

public interface ModelingService {

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    List<DecisionModel> listForTenant(String tenantId);

    DecisionModel getModelForTenant(Long id, String tenantId);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * @param model
     *            The new model.
     * @param tenantId
     *            The tenant to create the model for.
     * 
     * @return
     */
    DecisionModel createModelForTenant(DecisionModel model,
            String tenantId);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * @param id
     *            Name of the decision to be updated.
     * @param model
     *            The updated model.
     * @param tenantId
     *            The tenant whose model is to be updated.
     * 
     * @return
     */
    DecisionModel updateModelForTenant(Long id,
            DecisionModel model, String tenantId);

    /**
     * Delete the named model for the tenant.
     * @param id
     *            Name of a particular decision.
     * @param tenantId
     *            The tenant whose model is to be removed.
     */
    void deleteModelForTenant(Long id, String tenantId);

    Definitions convert(DecisionModel source);

}