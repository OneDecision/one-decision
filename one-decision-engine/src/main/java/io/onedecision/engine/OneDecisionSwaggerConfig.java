package io.onedecision.engine;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import com.google.common.base.Predicate;

@Component
public class OneDecisionSwaggerConfig {

    @Bean
    public Docket oneDecisionApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("one-decision-api")
                .select().paths(publicPaths())
                .build();
    }

    /**
     * 
     * @return public API.
     */
    @SuppressWarnings("unchecked")
    private Predicate<String> publicPaths() {
        return or(regex("/.*/onedecision.*"), regex("/.*/decision-models.*"),
                regex("/.*/decision-ui-models.*"), regex("/.*/domain.*"));
    }
}
