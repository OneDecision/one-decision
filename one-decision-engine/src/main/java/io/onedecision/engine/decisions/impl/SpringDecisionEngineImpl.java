package io.onedecision.engine.decisions.impl;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.web.DecisionController;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;
import io.onedecision.engine.decisions.web.DecisionUIModelController;

import org.springframework.stereotype.Component;

@Component
public class SpringDecisionEngineImpl extends AbstractDecisionEngineImpl
        implements DecisionEngine {

    public SpringDecisionEngineImpl() {
        setRepositoryService(new DecisionDmnModelController());
        setRuntimeService(new DecisionController());
        setModelingService(new DecisionUIModelController());
    }

}
