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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import io.onedecision.engine.TestApplication;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.test.MockMultipartFileUtil;

/**
 * Tests decision invoked as REST services.
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {TestApplication.class})
@WebAppConfiguration
public class DecisionControllerTest implements ExamplesConstants {

    @Autowired
    protected DecisionEngine decisionEngine;

    protected static DmnModel dmnModel;

    private DecisionDmnModelController svc;

    @Before
    public  void setUp() {
        svc = (DecisionDmnModelController) decisionEngine
                .getRepositoryService();
        assertClean(svc);
    }
    
    @After
    public  void tearDown() {
        assertClean(svc);
    }
    
    @Test
    public void testDecisionViaController() throws IOException,
            DecisionException {
        create();
        retrieve();
        execute();
        delete();
    }

    private void delete() {
        svc.deleteModelForTenant(ARR_DEFINITION_ID, TENANT_ID);
        assertEquals("Test environment should now contain 0 decisions", 0, svc
                .listForTenant(TENANT_ID).size());
    }

    private void execute() throws IOException {
        Map<String, Object> vars = new HashMap<String, Object>();
        String applicantVal = "{\"age\":18,\"health\":\"Good\"}";
        vars.put("applicant", applicantVal);
        Map<String, Object> results = decisionEngine.getRuntimeService()
                .executeDecision(ARR_DEFINITION_ID, ARR_DECISION_ID, vars,
                        TENANT_ID);
        assertNotNull(results);
        assertEquals(results.get("policy"), "{\"riskRating\":\"Low\"}");
    }

    private void retrieve() {
        assertEquals(
                "Test environment should now contain 1 decisions",
                1, svc.listForTenant(TENANT_ID).size());
    }

    private void create() throws IOException {
        dmnModel = svc.handleFileUpload(TENANT_ID,
                null/* no deployment message */,
                MockMultipartFileUtil.newInstance(ARR_DMN_RESOURCE));
    }

    private void assertClean(DecisionDmnModelController svc) {
        assertEquals(
                "Test environment should not contain any decisions at this point",
                0, svc.listForTenant(TENANT_ID).size());
    }
}
