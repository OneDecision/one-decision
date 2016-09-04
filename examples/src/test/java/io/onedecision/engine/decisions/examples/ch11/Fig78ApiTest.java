package io.onedecision.engine.decisions.examples.ch11;

import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.test.DecisionRule;

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
 * Tests the fluent API's ability to create the Calculate Discount example from
 * Fig 27 of spec (p66 in 1.0 beta 2 version).
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class Fig78ApiTest implements ExamplesConstants {

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    private static Ch11LoanExample ch11LoanExample;

    private String age;
    private String maritalStatus;
    private String employmentStatus;
    private int partialScore;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { 
                { "18", "S", "EMPLOYED", String.valueOf(32 + 25 + 45) },
                { "45", "M", "SELF-EMPLOYED", String.valueOf(43 + 45 + 36) }
        });
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ch11LoanExample = new Ch11LoanExample();
    }

    public Fig78ApiTest(String age, String maritalStatus,
            String employmentStatus, String partialScore) {
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.employmentStatus = employmentStatus;
        this.partialScore = Integer.valueOf(partialScore);
    }

    @Test
    public void testCalculateDiscount() throws Exception {
        decisionRule.getDecisionEngine().getRepositoryService()
                .createModelForTenant(getDmnModel());

        // vars.clear();
        // vars.put("Customer", customer);
        // vars.put("Ordersize", orderSize);
        // decisionRule.getDecisionEngine().getRuntimeService().executeDecision(FIG27_DEFINITION_ID,
        // FIG27_DECISION_ID, vars, TENANT_ID);
        // Assert.assertNotNull(vars.get("discount"));
    }

    // demonstrate Java API for defining decision.
    private DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition applicantData = decisionRule.getObjectFactory()
                    .createItemDefinition()
                    .withId("applicantData").withName("Applicant Data")
                    .withTypeRef(Ch11LoanExample.APPLICANT_DATA);
            ItemDefinition bureauData = decisionRule.getObjectFactory()
                    .createItemDefinition()
                    .withId("bureauData").withName("Bureau Data")
                    .withTypeRef(Ch11LoanExample.BUREAU_DATA);
            
            // build definitions container
            Definitions def = decisionRule
                    .getObjectFactory()
                    .createDefinitions()
                    .withId(CH11_FIG78_DEFINITION_ID)
                    .withDescription(
                            "Implements decision logic from Figure 78 (Application risk score model)")
                    .withItemDefinitions(applicantData, bureauData);

            DecisionTable dt = ch11LoanExample
                    .getApplicationRiskScoreModelDT();

            Decision d = decisionRule
                    .getObjectFactory()
                    .createDecision()
                    .withId("Application risk score model")
                    .withName("Application risk score model")
                    .withInformationItem(
                            decisionRule.getObjectFactory()
                                    .createInformationItem()
                                    .withId("bureauData"))
                    .withDecisionTable(dt);

            def.withDecisions(d);

            dm = new DmnModel(def, null, TENANT_ID);
        }
        return dm;
    }

}
