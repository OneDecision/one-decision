package link.omny.decisions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.impl.DecisionService;
import link.omny.decisions.model.Decision;
import link.omny.decisions.model.Definitions;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ApplicantRiskRatingTest {

    private static final String DMN_RESOURCE = "/ApplicationRiskRating.dmn";
    private static final String DECISION_ID = "DetermineApplicantRiskRating";
    private static DecisionService svc;
    private static DecisionModelFactory fact;

    private String applicant;
    private String policy;
    private Decision decision;
    private Map<String, String> vars = new HashMap<String, String>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { 
            { "{\"age\":65,\"health\":\"Bad\"}", "{\"riskRating\":\"High\"}" },
            { "{\"age\":60,\"health\":\"Good\"}", "{\"riskRating\":\"Medium\"}" },
            { "{\"age\":42,\"health\":\"Bad\"}", "{\"riskRating\":\"Medium\"}" },
            { "{\"age\":36,\"health\":\"Good\"}", "{\"riskRating\":\"Medium\"}" },
            { "{\"age\":24,\"health\":\"Bad\"}", "{\"riskRating\":\"Medium\"}" },
            { "{\"age\":18,\"health\":\"Good\"}", "{\"riskRating\":\"Low\"}" }
        });
    }

    @BeforeClass
    public static void setUpClass() {
        fact = new DecisionModelFactory();
        svc = new DecisionService();
    }

    public ApplicantRiskRatingTest(String applicant, String policy) {
        this.applicant = applicant;
        this.policy = policy;
    }

    @Test
    public void testApplicantRiskRating() {
        try {
            vars.clear();
            vars.put("applicant", applicant);
            vars = svc.execute(getDecision(), vars);
            assertEquals(policy, vars.get("conclusion"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

    private Decision getDecision() throws Exception {
        if (decision == null) {
            Definitions dm = fact.load(DMN_RESOURCE);
            assertNotNull("Unable to load decision model: " + DMN_RESOURCE);
            decision = dm.getDecisionById(DECISION_ID);
            assertNotNull("Unable to find decision with id: " + DECISION_ID,
                    decision);

            String script = svc.getScript(decision.getDecisionTable());
            System.out.println("Received script:\n" + script);
            assertTrue("Unable to compile decision table", script != null
                    && script.length() > 0);
        }
        return decision;
    }

}
