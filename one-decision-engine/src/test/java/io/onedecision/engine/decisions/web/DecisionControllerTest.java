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
package io.onedecision.engine.decisions.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.SpringDecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.test.MockMultipartFileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests decision invoked as REST services.
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class DecisionControllerTest implements ExamplesConstants {

    // @ClassRule
    // public static DecisionRule decisionRule = new DecisionRule();

    @Autowired
    protected static DecisionEngine decisionEngine;

    protected static DmnModel dmnModel;

    private static DecisionDmnModelController repoSvc;

    private static DecisionController runtimeSvc;

    @BeforeClass
    public static void setUp() throws Exception {
        decisionEngine = new SpringDecisionEngineImpl();
        repoSvc = (DecisionDmnModelController) decisionEngine
                .getRepositoryService();
        dmnModel = repoSvc.handleFileUpload(TENANT_ID,
                null/* no deployment message */,
                MockMultipartFileUtil.newInstance(ARR_DMN_RESOURCE));

        runtimeSvc = (DecisionController) decisionEngine.getRuntimeService();
    }


    @After
    public void tearDown() {
        repoSvc.deleteModelForTenant(dmnModel.getId(), TENANT_ID);
    }


    @Test
    public void testDecisionViaController() throws IOException,
            DecisionException {
        Map<String, Object> vars = new HashMap<String, Object>();
        String applicantVal = "{\"age\":18,\"health\":\"Good\"}";
        vars.put("applicant", applicantVal);
        String conclusion = runtimeSvc.execute(ARR_DEFINITION_ID,
                ARR_DECISION_ID, vars, TENANT_ID);
        assertNotNull(conclusion);
        assertTrue(conclusion.contains("{\"riskRating\":\"Low\"}"));
    }
}
