package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

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
     * @return HTML documentation produced from the model.
     */
    String getDocumentationForTenant(String definitionId, String tenantId);

    /**
     * @param definitionId
     *            The definition id in the DMN model.
     * @param tenantId
     *            The tenant owning the model.
     * @return Any image available for the model, may be null.
     */
    byte[] getImageForTenant(String definitionId, String tenantId);

    /**
     * Create DMN model in the repository for the specified tenant.
     * 
     * @param tenantId
     *            The tenant to create the model for.
     * @param is
     *            Stream to read DNM from; will be closed after use.
     * @return The requested model.
     */
    DmnModel createModelForTenant(String tenantId, InputStream is)
            throws IOException;

    /**
     * Create DMN model in the repository for the specified tenant.
     * 
     * @param model
     *            The new model.
     * @return The stored model.
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
     * Validate the model, violations include schema, specification and
     * suitability for execution.
     * 
     * @param dm
     *            DMN model
     * @return violations
     */
    Set<ConstraintViolation<Definitions>> validate(Definitions dm)
            throws IOException;

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