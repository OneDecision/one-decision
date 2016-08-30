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
package io.onedecision.engine.decisions.examples.ch11;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.api.RuntimeService;
import io.onedecision.engine.test.DecisionRule;
import io.onedecision.engine.test.Deployment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the engine's ability to run the Strategy model from a JSON
 * serialisation.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
@Deployment(resources = { "/decisions/examples/Fig70Strategy.json" },
    domainModelUri = "http://onedecision.io/loans", 
    domainModelUrl = "/domains/loans.json", 
    tenantId = "examples")
public class StrategyJsonTest implements ExamplesConstants {

    private String eligibility;
    private String bureauCallType;
    private Map<String, Object> vars = new HashMap<String, Object>();
    private String strategy;

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                { "INELIGIBLE", "", "DECLINE" }, 
                { "ELIGIBLE", "FULL", "BUREAU" },
                // { "ELIGIBLE", "MINI", "BUREAU" },
                { "ELIGIBLE", "NONE", "THROUGH" }
        });
    }

    @BeforeClass
    public static void setUpClass() {
        // de = new InMemoryDecisionEngineImpl();
    }

    public StrategyJsonTest(String eligibility, String bureauCallType,
            String strategy) {
        this.eligibility = eligibility;
        this.bureauCallType = bureauCallType;
        this.strategy = strategy;
    }

    @Test
    public void testStrategyDecision() {
        RuntimeService svc = decisionRule.getDecisionEngine()
                .getRuntimeService();
        try {
            vars.clear();
            vars.put("eligibility", eligibility);
            vars.put("bureauCallType", bureauCallType);
            vars = svc.executeDecision(CH11_FIG70_DEFINITION_ID,
                    CH11_FIG70_DECISION_ID, vars, TENANT_ID);
            assertEquals(strategy, vars.get("strategy"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

}
