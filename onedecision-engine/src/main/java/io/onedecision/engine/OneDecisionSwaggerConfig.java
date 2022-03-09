package io.onedecision.engine;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Provides Swagger information about the REST API.
 *
 * @author Tim Stephenson
 */
@Component
public class OneDecisionSwaggerConfig {

    @Bean
    public Docket oneDecisionApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("decision-api")
                .apiInfo(apiInfo())
                .select()
                .paths(publicPaths())
                .build();
    }

    /**
     * @return REST API paths.
     */
    @SuppressWarnings("unchecked")
    private Predicate<String> publicPaths() {
        return or(
                regex("/.*/decisions.*"),
                regex("/.*/decision-models.*"),
                regex("/.*/domain.*")
               );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Decision Model Management API")
                .description("Manage and invoke decision models.")
                .license("Apache License Version 2.0")
                .licenseUrl("LICENSE-2.0.html")
                .version("3.0").build();
    }
}
