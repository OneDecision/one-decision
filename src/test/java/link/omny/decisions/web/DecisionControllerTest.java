package link.omny.decisions.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import link.omny.decisions.Application;
import link.omny.decisions.api.DecisionsException;
import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.model.dmn.DmnModel;
import link.omny.decisions.test.MockMultipartFileUtil;

import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests decision invoked from Activiti process engine.
 * 
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DecisionControllerTest implements ExamplesConstants {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("test-activiti.cfg.xml");

    @Autowired
    protected DecisionDmnModelController svc;

    @Autowired
    protected DecisionController decisionController;

    protected DmnModel dmnModel;

    @Before
    public void setUp() throws IOException {
        dmnModel = svc.handleFileUpload(TENANT_ID,
                null/* no deployment message */,
                MockMultipartFileUtil.newInstance(ARR_DMN_RESOURCE));
    }

    @After
    public void tearDown() {
        svc.deleteModelForTenant(TENANT_ID, dmnModel.getId());
    }


    @Test
    public void testDecisionViaController() throws IOException,
            DecisionsException {
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("applicant", "{\"age\":18,\"health\":\"Good\"}");
        String conclusion = decisionController.executeDecision(TENANT_ID,
                ARR_DEFINITION_ID, ARR_DECISION_ID, vars);
        assertNotNull(conclusion);
        assertEquals("{\"riskRating\":\"Low\"}", conclusion);
    }
}
