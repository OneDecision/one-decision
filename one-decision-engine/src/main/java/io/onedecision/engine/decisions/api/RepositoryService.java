package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface RepositoryService {

    /**
     * Return just the decision models for a specific tenant.
     * 
     * @param tenantId
     *            .
     * @return decision models for tenantId.
     */
    List<DmnModel> listForTenant(String tenantId);

    DmnModel getModelForTenant(String tenantId, Long id);

    String getDmnForTenant(String tenantId, String id);

    byte[] getImageForTenant(String tenantId, String id);

    /**
     * Model updates are typically additive but for the time being at least this
     * is not enforced.
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
     * @return
     * @throws IOException
     */
    DmnModel createModelForTenant(Definitions model, String deploymentMessage,
            byte[] image, String tenantId);

    DmnModel createModelForTenant(DmnModel model);

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
    void updateModelForTenant(String tenantId, String definitionId,
            DmnModel model);

    /**
     * Delete the specified model for the tenant.
     * 
     * @param tenantId
     *            The tenant whose model is to be removed.
     * @param id
     *            Id of a particular decision (allocated by the repository not
     *            any id from within the DMN).
     */
    void deleteModelForTenant(String tenantId, Long id);

    void write(Definitions dm, Writer out) throws IOException;

}