package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Manage the DMN models known to the engine.
 *
 * @author Tim Stephenson
 */
public interface RepositoryService {

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            The tenant whose models should be returned. .
     * @return decision models for tenantId.
     */
    List<DmnModel> listForTenant(String tenantId);

    /**
     * @param definitionId
     *            The definition id in the DMN model.
     * @param tenantId
     *            The tenant owning the model.
     * @return The requested model.
     */
    DmnModel getModelForTenant(String definitionId, String tenantId);

    /**
     * @param definitionId
     *            The definition id in the DMN model.
     * @param tenantId
     *            The tenant owning the model.
     * @return The model as DMN XML.
     */
    String getDmnForTenant(String definitionId, String tenantId);

    /**
     * @param definitionId
     *            The definition id in the DMN model.
     * @param tenantId
     *            The tenant owning the model.
     * @return Any image available for the model, may be null.
     */
    byte[] getImageForTenant(String definitionId, String tenantId);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param model
     *            The new model.
     * @param deploymentMessage
     *            Message to store alongside the deployment for example
     *            including a change control reference (may be null).
     * @param image
     *            Graphical representation of the decision (may be null).
     * @param tenantId
     *            The tenant to create the model for.
     * 
     * @return The requested model.
     */
    DmnModel createModelForTenant(DmnModel model);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
     * 
     * @param definitionId
     *            The id of the DMN root element.
     * @param model
     *            The updated model.
     * @param tenantId
     *            The tenant whose model is to be updated.
     */
    void updateModelForTenant(String definitionId, DmnModel model,
            String tenantId);

    /**
     * Delete the specified model for the tenant.
     * 
     * @param id
     *            Id of a particular decision (allocated by the repository not
     *            any id from within the DMN).
     * @param tenantId
     *            The tenant whose model is to be removed.
     */
    void deleteModelForTenant(Long id, String tenantId);

    /**
     * Delete the specified model for the tenant.
     * 
     * @param definitionId
     *            The definition id in the DMN model.
     * @param tenantId
     *            The tenant owning the model.
     */
    void deleteModelForTenant(String definitionId, String tenantId);

    /**
     * Experimental. TBD if this is appropriate on the API.
     * 
     * @param dm
     *            DMN model
     * @param out
     *            Writer to serialise to.
     */
    void write(Definitions dm, Writer out) throws IOException;

}