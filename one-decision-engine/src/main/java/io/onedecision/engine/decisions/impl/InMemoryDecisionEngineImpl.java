package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionEngine;

public class InMemoryDecisionEngineImpl extends AbstractDecisionEngineImpl
        implements DecisionEngine {

    public InMemoryDecisionEngineImpl() {
        setRepositoryService(new DecisionModelFactory());
        DecisionService runtimeSvc = new DecisionService();
        runtimeSvc.setDecisionEngine(this);
        setRuntimeService(runtimeSvc);
    }

}
