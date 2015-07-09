package link.omny.decisions.api;

import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.web.DecisionDmnModelController;

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
@ComponentScan(basePackages = { "link.omny.decisions", "link.omny.domain" })
// @EnableAutoConfiguration
@EntityScan({ "link.omny.decisions", "link.omny.domain" })
@EnableJpaRepositories({ "link.omny.domain.repositories",
        "link.omny.decisions.repositories" })
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
