package io.onedecision.engine;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.SpringDecisionEngineImpl;
import io.onedecision.engine.decisions.web.DecisionController;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("decisionController")
    private DecisionController runSvc;

    @Bean
    protected DecisionEngine decisionEngine() {
        SpringDecisionEngineImpl de = new SpringDecisionEngineImpl();
        de.setRepositoryService(repoSvc);
        de.setRuntimeService(runSvc);
        return de;
    }
}
