package io.onedecision.engine.decisions.api;

/**
 * Java entry point to all engine functionality.
 * 
 * <p>
 * Please note that the engine also exposes a REST API that is documented
 * separately.
 *
 * @author Tim Stephenson
 */
public interface DecisionEngine {

    ModelingService getModelingService();

    RepositoryService getRepositoryService();

    RuntimeService getRuntimeService();
}
