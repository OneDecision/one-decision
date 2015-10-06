package io.onedecision.engine;

import io.onedecision.engine.decisions.impl.DecisionService;
import io.onedecision.engine.decisions.impl.del.DelExpression;
import io.onedecision.engine.decisions.impl.del.DurationExpression;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ComponentScan({ "io.onedecision.engine" })
@EnableAutoConfiguration
// @EntityScan({ "io.onedecision.engine" })
// @EnableJpaRepositories({ "io.onedecision.engine" })
public class OneDecisionConfig {

    @Bean
    public DecisionService decisionService() {
        DecisionService svc = new DecisionService();
        List<DelExpression> compilers = new ArrayList<DelExpression>();
        compilers.add(new DurationExpression());
        svc.setDelExpressions(compilers);
        return svc;
    }
}
