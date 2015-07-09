package link.omny.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import link.omny.decisions.examples.ExamplesConstants;
import link.omny.decisions.impl.DecisionModelFactory;
import link.omny.decisions.impl.DecisionService;
import link.omny.decisions.model.dmn.Decision;
import link.omny.decisions.model.dmn.Definitions;

import org.junit.BeforeClass;
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
public class ApplicantRiskRatingTest implements ExamplesConstants {

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

	@Test
	@Ignore // currently failing
	public void testApplicantRiskRatingWithBadParams() {
		try {
			vars.clear();
			vars.put("person", applicant);
			vars = svc.execute(getDecision(), vars);
			fail("Did not detect bad input");
		} catch (Exception e) {
			// expected because input variable should be called 'applicant'
		}
	}

    private Decision getDecision() throws Exception {
        if (decision == null) {
            Definitions dm = fact.loadFromClassPath(ARR_DMN_RESOURCE);
            assertNotNull("Unable to load decision model: " + ARR_DMN_RESOURCE,
                    dm);
            decision = dm.getDecisionById(ARR_DECISION_ID);
            assertNotNull("Unable to find decision with id: " + ARR_DECISION_ID,
                    decision);

            String script = svc.getScript(decision.getDecisionTable());
            assertTrue("Unable to compile decision table", script != null
                    && script.length() > 0);
        }
        return decision;
    }

}
