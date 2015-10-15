package io.onedecision.engine;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.SpringDecisionEngineImpl;
import io.onedecision.engine.decisions.web.DecisionController;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;
import io.onedecision.engine.decisions.web.DecisionUIModelController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration to bootstrap the engine.
 *
 * @author Tim Stephenson
 */
@Configuration
@EnableAutoConfiguration
public class OneDecisionConfig {

    @Autowired
    private DecisionDmnModelController repoSvc;

    @Autowired
    private DecisionController runSvc;

    @Autowired
    private DecisionUIModelController modelingSvc;

    @Bean
    protected DecisionEngine decisionEngine() {
        SpringDecisionEngineImpl de = new SpringDecisionEngineImpl();
        de.setRepositoryService(repoSvc);
        de.setRuntimeService(runSvc);
        de.setModelingService(modelingSvc);
        return de;
    }
}
