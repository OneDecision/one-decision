package io.onedecision.engine.domain;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.onedecision.engine.domain.api.DomainModelFactory;
import io.onedecision.engine.domain.impl.ClasspathDomainModelFactory;

/**
 * Spring configuration for the onedecision-domain module.
 *
 * @author Tim Stephenson
 */
@Configuration
@EnableAutoConfiguration
@EntityScan({ "io.onedecision.engine.domain.model" })
@EnableJpaRepositories({ "io.onedecision.engine.domain.repositories" })
public class OneDecisionDomainConfig {

    @Bean
    protected DomainModelFactory domainModelFactory() throws IOException {
        return new ClasspathDomainModelFactory();
    }
}
