package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.onedecision.engine.decisions.api.DecisionModelFactory;
import io.onedecision.engine.decisions.api.DecisionService;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.BusinessKnowledgeModel;
import io.onedecision.engine.decisions.model.dmn.Clause;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionRule;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.Text;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.decisions.model.dmn.validators.SchemaValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the engine's ability to run the EmailFollowUp model from a DMN
 * serialisation.
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class EmailFollowUpApiTest implements ExamplesConstants {

    private static ObjectFactory objFact;

    private static DecisionService svc;

	private static DecisionModelFactory decisionModelFactory;

	private static SchemaValidator schemaValidator;

    private String contactInstance;
    private String emailInstance;
    private Decision decision;
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
        svc = new DecisionService();
		decisionModelFactory = new DecisionModelFactory();
		schemaValidator = new SchemaValidator();
    }

    public EmailFollowUpApiTest(String contact, String email) {
        this.contactInstance = contact;
        this.emailInstance = email;
    }

    @Test
    @Ignore
    public void testEmailFollowUp() {
        try {
            vars.clear();
            vars.put("contact", contactInstance);
            vars = svc.execute(getDecision(), vars);
            assertEquals(emailInstance, vars.get("conclusion"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

    private Decision getDecision() throws Exception {
        if (decision == null) {
            // demonstrate Java API for defining decision.
            Definitions dm = objFact.createTDefinitions();
            dm.setId(EFU_DEFINITION_ID);
            dm.setDescription("A decision model to choose how to follow up a customer lead");

            ItemDefinition contactDef = objFact.createTItemDefinition();
            contactDef.setDescription("A contact (person) to follow up");
            contactDef.setId("contactDef");
            contactDef.setName("Contact Definition");
            dm.getItemDefinition().add(contactDef);

            ItemDefinition emailDef = objFact.createTItemDefinition();
            emailDef.setDescription("Define email to send as follow up");
            emailDef.setId("emailDef");
            emailDef.setName("Email Definition");
            dm.getItemDefinition().add(emailDef);

            BusinessKnowledgeModel knowledgeModel = objFact
                    .createTBusinessKnowledgeModel();
            InformationItem contact = objFact.createTInformationItem();
            contact.setDescription("The contact to pass to the decision");
            contact.setId("contact");
            contact.setItemDefinition(contactDef.getTypeRef());
            contact.setName("Contact");
            knowledgeModel.getInformationItem().add(contact);

            InformationItem email = objFact.createTInformationItem();
            email.setDescription("The email to be selected by the decision");
            email.setId("email");
            email.setItemDefinition(emailDef.getTypeRef());
            email.setName("Email");
            knowledgeModel.getInformationItem().add(email);

            dm.setBusinessKnowledgeModel(knowledgeModel);

            Decision d = objFact.createTDecision();
            d.setId(EFU_DECISION_ID);
            d.setName("Determine Email Follow Up");

            DecisionTable dt = objFact.createTDecisionTable();
            dt.setHitPolicy(HitPolicy.UNIQUE);
            dt.setDescription("DT to determine Email Follow Up");

            Clause clause1 = objFact.createTClause();
            clause1.setName("timeSinceLogin");

            LiteralExpression input1 = objFact.createTLiteralExpression();
            input1.setDescription("Time since login is less than 14 days");
            input1.setId("timeSinceLoginLT14Days");
            input1.setItemDefinition(contactDef.getTypeRef());
			Text text = objFact.createTLiteralExpressionText();
			text.getContent().add("contact.timeSinceLogin < P14D");
			input1.setText(text);
            clause1.getInputEntry().add(input1);

            LiteralExpression input2 = objFact.createTLiteralExpression();
            input2.setDescription("Time since login is at least 14 days");
            input2.setId("timeSinceLoginGE14Days");
            input2.setItemDefinition(contactDef.getTypeRef());
			text = objFact.createTLiteralExpressionText();
			text.getContent().add("contact.timeSinceLogin >= P14D");
			input2.setText(text);
			clause1.getInputEntry().add(input2);

            dt.getClause().add(clause1);

            Clause clause2 = objFact.createTClause();
            clause2.setName("timeSinceEmail");

            LiteralExpression input3 = objFact.createTLiteralExpression();
            input3.setDescription("Time since email is less than 7 days");
            input3.setId("timeSinceEmailLT7Days");
            input3.setItemDefinition(contactDef.getTypeRef());
			text = objFact.createTLiteralExpressionText();
			text.getContent().add("contact.timeSinceEmail < P7D");
			input3.setText(text);
            clause2.getInputEntry().add(input3);

            LiteralExpression input4 = objFact.createTLiteralExpression();
            input4.setDescription("Time since email is at least 7 days");
            input4.setId("timeSinceEmailGE7Days");
            input4.setItemDefinition(contactDef.getTypeRef());
			text = objFact.createTLiteralExpressionText();
			text.getContent().add("contact.timeSinceEmail < P7D");
			input4.setText(text);
            clause2.getInputEntry().add(input4);

            dt.getClause().add(clause2);

            Clause clause4 = objFact.createTClause();
            clause4.setName("template");

            LiteralExpression output1 = objFact.createTLiteralExpression();
            output1.setDescription("Email template to use is 'MissedYou'");
            output1.setId("templateNone");
            output1.setItemDefinition(emailDef.getTypeRef());
			text = objFact.createTLiteralExpressionText();
			text.getContent().add("email.template = 'None'");
			output1.setText(text);
            clause4.getInputEntry().add(output1);

            LiteralExpression output2 = objFact.createTLiteralExpression();
            output2.setDescription("Email template to use is 'MissedYou'");
            output2.setId("templateMissedYou");
            output2.setItemDefinition(contactDef.getTypeRef());
			text = objFact.createTLiteralExpressionText();
			text.getContent().add("email.template = 'MissedYou'");
			output2.setText(text);
            clause4.getInputEntry().add(output2);

            dt.getClause().add(clause4);

            DecisionRule rule1 = objFact.createTDecisionRule();
            rule1.getConditions().add(input1);
            rule1.getConditions().add(input3);
            rule1.getConclusions().add(output1);
            dt.getRule().add(rule1);

            DecisionRule rule2 = objFact.createTDecisionRule();
            rule2.getConditions().add(input2);
            rule2.getConditions().add(input4);
            rule2.getConclusions().add(output2);
            dt.getRule().add(rule2);
            assertEquals(2, dt.getRule().size());
            
            d.setDecisionTable(dt);
            dm.addDecision(d);

            assertSerializationProduced(dm);
            decision = dm.getDecisionById(EFU_DECISION_ID);
            assertNotNull(
                    "Unable to find decision with id: " + EFU_DECISION_ID,
                    decision);

            String script = svc.getScript(decision.getDecisionTable());
            System.out.println("Received script:\n" + script);
            assertTrue("Unable to compile decision table", script != null
                    && script.length() > 0);
        }
        return decision;
    }

	private void assertSerializationProduced(Definitions dm)
			throws IOException, FileNotFoundException {
		File dmnFile = new File("target", EFU_DEFINITION_ID + ".dmn");
		decisionModelFactory.write("application/xml", dm, dmnFile);
		System.out.println("Wrote dmn to: " + dmnFile);
		assertTrue(dmnFile.exists());

		InputStream fis = null;
		try {
			fis = new FileInputStream(dmnFile);
            DmnValidationErrors errors = new DmnValidationErrors();
			schemaValidator.validate(fis, errors);
			assertTrue(!errors.hasErrors());
		} finally {
            try {
                fis.close();
            } catch (Exception e) {
                ;
            }
		}
	}

}
