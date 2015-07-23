/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
