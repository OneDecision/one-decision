package io.onedecision.engine.decisions.api;

import io.onedecision.engine.decisions.web.DecisionDmnModelController;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 
 * @author Tim Stephenson
 */
@Configuration
@ComponentScan(basePackages = { "io.onedecision.engine.decisions",
		"io.onedecision.engine.domain" })
@EntityScan({ "io.onedecision.engine.decisions", "io.onedecision.engine.domain" })
@EnableJpaRepositories({ "io.onedecision.engine.domain.repositories",
		"io.onedecision.engine.decisions.repositories" })
public class TestContext {

    @Bean
    public DecisionModelFactory decisionModelFactory() {
        return new DecisionModelFactory();
    }

    @Bean
    DecisionDmnModelController decisionDmnModelController() {
        return new DecisionDmnModelController();
    }
}
