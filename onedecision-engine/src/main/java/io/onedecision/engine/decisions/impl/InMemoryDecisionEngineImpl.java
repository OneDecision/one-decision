package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionEngine;

public class InMemoryDecisionEngineImpl extends AbstractDecisionEngineImpl
        implements DecisionEngine {

    public InMemoryDecisionEngineImpl() {
        super();
        setRepositoryService(new DecisionModelFactory());
        DecisionService runtimeSvc = new DecisionService();
        runtimeSvc.setDecisionEngine(this);
        setRuntimeService(runtimeSvc);
    }

}
