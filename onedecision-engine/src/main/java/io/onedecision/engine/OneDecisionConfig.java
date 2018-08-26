package io.onedecision.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.impl.SpringDecisionEngineImpl;
import io.onedecision.engine.decisions.web.DecisionController;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;

/**
 * Spring configuration to bootstrap the engine.
 *
 * @author Tim Stephenson
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "io.onedecision.engine" })
@EntityScan({ "io.onedecision.engine.decisions.model" })
@EnableJpaRepositories({ "io.onedecision.engine.decisions.repositories" })
public class OneDecisionConfig {

    @Autowired
    @Qualifier("decisionDmnModelController")
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
