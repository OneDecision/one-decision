package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.DecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.Clause;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.web.DecisionDmnModelController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the fluent API's ability to create the Calculate Discount example.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class CalculateDiscountApiTest implements ExamplesConstants {

    private static ObjectFactory objFact;

    private static DecisionEngine de;

    private String contactInstance;
    private String emailInstance;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { 
 {
						"{\"timeSinceLogin\":\"P15D\",\"timeSinceEmail\":\"P8D\", \"doNotEmail\": false}",
                    "{\"template\":\"MissedYou\", \"subject\":\"We've missed you\"}" }
        });
    }

    @BeforeClass
    public static void setUpClass() {
        objFact = new ObjectFactory();

        de = new DecisionEngineImpl()
                .setRepositoryService(new DecisionDmnModelController());
    }

    public CalculateDiscountApiTest(String contact, String email) {
        this.contactInstance = contact;
        this.emailInstance = email;
    }

    @Test
    public void testCalculateDiscount() throws Exception {
            de.getRepositoryService().createModelForTenant(getDecision());

            vars.clear();
            // vars.put("contact", contactInstance);
            // de.getRuntimeService().executeDecision(CD_DEFINITION_ID,
            // CD_DECISION_ID, vars, TENANT_ID);
            // assertEquals(emailInstance, vars.get("conclusion"));
    }

    private DmnModel getDecision() throws Exception {
        if (dm == null) {
            // demonstrate Java API for defining decision.
            Definitions def = objFact
                    .createDefinitions()
                    .withId(CD_DEFINITION_ID)
                    .withDescription(
                            "A decision model to choose how to follow up a customer lead");

            ItemDefinition contactDef = objFact.createItemDefinition();
            contactDef.setDescription("A contact (person) to follow up");
            contactDef.setId("contactDef");
            contactDef.setName("Contact Definition");
            def.getItemDefinition().add(contactDef);


            // InformationItem email = objFact.createTInformationItem();
            // email.setDescription("The email to be selected by the decision");
            // email.setId("email");
            // email.setItemDefinition(emailDef.getTypeRef());
            // email.setName("Email");


            DecisionTable dt = objFact.createDecisionTable();
            dt.setHitPolicy(HitPolicy.UNIQUE);
            dt.setDescription("DT to determine Email Follow Up");

            Clause clause1 = objFact.createClause();
            clause1.setName("timeSinceLogin");

            LiteralExpression input1 = objFact.createLiteralExpression();
            input1.setDescription("Time since login is less than 14 days");
            input1.setId("timeSinceLoginLT14Days");
            // TODO
            // input1.setItemDefinition(contactDef.getTypeRef());
            // Text text = objFact.createTLiteralExpressionText();
            // text.getContent().add("contact.timeSinceLogin < P14D");
            // input1.setText(text);
            // clause1.getInputEntry().add(input1);

            LiteralExpression input2 = objFact.createLiteralExpression();
            input2.setDescription("Time since login is at least 14 days");
            input2.setId("timeSinceLoginGE14Days");
            // TODO
            // input2.setItemDefinition(contactDef.getTypeRef());
            // text = objFact.createTLiteralExpressionText();
            // text.getContent().add("contact.timeSinceLogin >= P14D");
            // input2.setText(text);
            // clause1.getInputEntry().add(input2);

            // dt.getClause().add(clause1);
            //
            // Clause clause2 = objFact.createTClause();
            // clause2.setName("timeSinceEmail");
            //
            // LiteralExpression input3 = objFact.createTLiteralExpression();
            // input3.setDescription("Time since email is less than 7 days");
            // input3.setId("timeSinceEmailLT7Days");
            // input3.setItemDefinition(contactDef.getTypeRef());
            // text = objFact.createTLiteralExpressionText();
            // text.getContent().add("contact.timeSinceEmail < P7D");
            // input3.setText(text);
            // clause2.getInputEntry().add(input3);
            //
            // LiteralExpression input4 = objFact.createTLiteralExpression();
            // input4.setDescription("Time since email is at least 7 days");
            // input4.setId("timeSinceEmailGE7Days");
            // input4.setItemDefinition(contactDef.getTypeRef());
            // text = objFact.createTLiteralExpressionText();
            // text.getContent().add("contact.timeSinceEmail < P7D");
            // input4.setText(text);
            // clause2.getInputEntry().add(input4);
            //
            // dt.getClause().add(clause2);
            //
            // Clause clause4 = objFact.createTClause();
            // clause4.setName("template");
            //
            // LiteralExpression output1 = objFact.createTLiteralExpression();
            // output1.setDescription("Email template to use is 'MissedYou'");
            // output1.setId("templateNone");
            // output1.setItemDefinition(emailDef.getTypeRef());
            // text = objFact.createTLiteralExpressionText();
            // text.getContent().add("email.template = 'None'");
            // output1.setText(text);
            // clause4.getInputEntry().add(output1);
            //
            // LiteralExpression output2 = objFact.createTLiteralExpression();
            // output2.setDescription("Email template to use is 'MissedYou'");
            // output2.setId("templateMissedYou");
            // output2.setItemDefinition(contactDef.getTypeRef());
            // text = objFact.createTLiteralExpressionText();
            // text.getContent().add("email.template = 'MissedYou'");
            // output2.setText(text);
            // clause4.getInputEntry().add(output2);
            //
            // dt.getClause().add(clause4);
            //
            // DecisionRule rule1 = objFact.createTDecisionRule();
            // rule1.getConditions().add(input1);
            // rule1.getConditions().add(input3);
            // rule1.getConclusions().add(output1);
            // dt.getRule().add(rule1);
            //
            // DecisionRule rule2 = objFact.createTDecisionRule();
            // rule2.getConditions().add(input2);
            // rule2.getConditions().add(input4);
            // rule2.getConclusions().add(output2);
            // dt.getRule().add(rule2);
            // assertEquals(2, dt.getRule().size());
            
            // d.setDecisionTable(dt);

            Decision d = objFact.createDecision()
                    .withId(CD_DECISION_ID)
                    .withName("Determine Email Follow Up");
            // .withDecisionTable(dt);
            // def.addDecision(d);
            def.withDecision(d);

            assertSerializationProduced(def);
            // Decision decision = def.getDecisionById(CD_DECISION_ID);
            // assertNotNull("Unable to find decision with id: " +
            // CD_DECISION_ID,
            // decision);

            dm = new DmnModel(def, null, TENANT_ID);
        }
        return dm;
    }

    // String script = svc.getScript(decision.getDecisionTable());
    // System.out.println("Received script:\n" + script);
    // assertTrue("Unable to compile decision table", script != null
    // && script.length() > 0);

    private void assertSerializationProduced(Definitions dm)
			throws IOException, FileNotFoundException {
        assertNotNull("Definitions produced must not be null", dm);

        File dmnFile = new File("target", CD_DEFINITION_ID + ".dmn");
        FileWriter out = new FileWriter(dmnFile);
        try {
            de.getRepositoryService().write(dm, out);
        } finally {
            out.close();
        }
		System.out.println("Wrote dmn to: " + dmnFile);
		assertTrue(dmnFile.exists());

		InputStream fis = null;
		try {
			fis = new FileInputStream(dmnFile);
            DmnValidationErrors errors = new DmnValidationErrors();
            // schemaValidator.validate(fis, errors);
			assertTrue(!errors.hasErrors());
		} finally {
            try {
                fis.close();
            } catch (Exception e) {
                ;
            }
		}
	}

    private void assertNotNull(String string, Definitions dm2) {
        // TODO Auto-generated method stub

    }

}
