package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.util.List;

/**
 * Support for creating new decision models.
 * 
 * <p>
 * This service constructs {@link io.onedecision.engine.decisions.model.ui }
 * beans and supports conversion to DMN models.
 *
 * @author Tim Stephenson
 */
public interface ModelingService {

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            The tenant whose models should be returned.
     * @return decision models for tenantId.
     */
    List<DecisionModel> listForTenant(String tenantId);

    DecisionModel getModelForTenant(Long id, String tenantId);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param model
     *            The new model.
     * @param tenantId
     *            The tenant to create the model for.
     * 
     * @return design time decision model.
     */
    DecisionModel createModelForTenant(DecisionModel model,
            String tenantId);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param id
     *            Name of the decision to be updated.
     * @param model
     *            The updated model.
     * @param tenantId
     *            The tenant whose model is to be updated.
     * 
     * @return design time decision model.
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

    /**
     * Convert from the design time model to a DMN model.
     * 
     * @param source
     *            design time decision model.
     * @return DMN decision model (definitions root element).
     */
    Definitions convert(DecisionModel source);

}