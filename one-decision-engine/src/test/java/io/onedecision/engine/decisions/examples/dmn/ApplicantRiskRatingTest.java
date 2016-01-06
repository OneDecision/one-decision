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
package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.api.RuntimeService;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.test.DecisionRule;
import io.onedecision.engine.test.Deployment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the engine's ability to run the ApplicationRiskRating model from a DMN
 * serialisation.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
@Deployment(resources = { "/decisions/examples/ApplicationRiskRating.dmn" }, tenantId = "examples")
public class ApplicantRiskRatingTest implements ExamplesConstants {

    private String applicant;
    private String policy;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                { "{\"age\":65,\"health\":\"Bad\"}",
                        "{\"riskRating\":\"High\"}" },
                { "{\"age\":60,\"health\":\"Good\"}",
                        "{\"riskRating\":\"Medium\"}" },
                // { "{\"age\":42,\"health\":\"Bad\"}",
                // "{\"riskRating\":\"Medium\"}" },
                // { "{\"age\":36,\"health\":\"Good\"}",
                // "{\"riskRating\":\"Medium\"}" },
                { "{\"age\":24,\"health\":\"Bad\"}",
                        "{\"riskRating\":\"Medium\"}" },
                { "{\"age\":18,\"health\":\"Good\"}",
                        "{\"riskRating\":\"Low\"}" } });
    }

    @BeforeClass
    public static void setUpClass() {
        // de = new InMemoryDecisionEngineImpl();
    }

    public ApplicantRiskRatingTest(String applicant, String policy) {
        this.applicant = applicant;
        this.policy = policy;
    }

    @Test
    public void testApplicantRiskRating() {
        RuntimeService svc = decisionRule.getDecisionEngine()
                .getRuntimeService();
        try {
            vars.clear();
            vars.put("applicant", applicant);
            vars = svc.executeDecision(ARR_DEFINITION_ID, ARR_DECISION_ID,
                    vars, TENANT_ID);
            assertEquals(policy, vars.get("policy"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

    @Test
    @Ignore
    // TODO currently failing
    public void testApplicantRiskRatingWithBadParams() {
        RuntimeService svc = decisionRule.getDecisionEngine()
                .getRuntimeService();
        try {
            vars.clear();
            vars.put("person", applicant);
            vars = svc.executeDecision(ARR_DEFINITION_ID, ARR_DECISION_ID,
                    vars, TENANT_ID);
            fail("Did not detect bad input");
        } catch (Exception e) {
            // expected because input variable should be called 'applicant'
        }
    }

}
