package io.onedecision.engine.decisions.api;

import java.io.IOException;
import java.util.Map;

/**
 * Invoke decisions known to the engine.
 *
 * @see RepositoryService for how to register models.
 * @author Tim Stephenson
 */
public interface RuntimeService {

    /**
     * Executes the decision in the specified definitions bundle.
     * 
     * @param definitionId
     *            Id for the decision bundle or package. This is the id of the
     *            DMN file's root definitions element.
     * @param decisionId
     *            Id of a particular decision in the bundle.
     * @param params
     *            <code>Map</code> of parameters expected as input to the
     *            specified decision. Values are expected to be JSON serialized.
     * @return JSON serialised output from the specified decision.
     */
    Map<String, Object> executeDecision(String definitionId,
            String decisionId, Map<String, Object> params, String tenantId)
            throws IOException, DecisionException;

}