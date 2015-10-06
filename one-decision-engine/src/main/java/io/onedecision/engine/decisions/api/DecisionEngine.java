package io.onedecision.engine.decisions.api;

/**
 * Entry point to all engine functionality.
 *
 * @author Tim Stephenson
 */
public interface DecisionEngine {

    ModelingService getModelingService();

    RepositoryService getRepositoryService();

    RuntimeService getRuntimeService();
}
