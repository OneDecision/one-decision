package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.test.DecisionRule;
import io.onedecision.engine.test.Deployment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the engine's ability to run a simple email follow up example model.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
@Deployment(resources = { "/decisions/examples/EmailFollowUp.dmn" }, tenantId = "examples")
public class EmailFollowUpTest implements ExamplesConstants {

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

	private static final long ONE_DAY = 24 * 60 * 60 * 1000;

	private String contactInstance;
	private String emailInstance;
    private Map<String, Object> vars = new HashMap<String, Object>();

	@Parameters
	public static Collection<String[]> data() {
		return Arrays
				.asList(new String[][] { {
						"{\"timeSinceLogin\":" + (15 * ONE_DAY)
								+ ",\"timeSinceEmail\":" + (8 * ONE_DAY)
								+ ", \"doNotEmail\": false}",
                        "{\"template\":\"MissedYou\",\"subject\":\"We've missed you\"}" } });
	}

	public EmailFollowUpTest(String contact, String email) {
		this.contactInstance = contact;
		this.emailInstance = email;
	}

	@Test
	public void testEmailFollowUp() {
		try {
			vars.clear();
			vars.put("contact", contactInstance);
            vars = decisionRule
                    .getDecisionEngine()
                    .getRuntimeService()
                    .executeDecision(EFU_DEFINITION_ID, EFU_DECISION_ID,
                    vars, TENANT_ID);
			assertEquals(emailInstance, vars.get("email"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

}
