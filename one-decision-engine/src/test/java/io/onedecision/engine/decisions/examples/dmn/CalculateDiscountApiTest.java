package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.DecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
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
        return Arrays
                .asList(new String[][] { {
                        "{\"timeSinceLogin\":\"P15D\",\"timeSinceEmail\":\"P8D\", \"doNotEmail\": false}",
                        "{\"template\":\"MissedYou\", \"subject\":\"We've missed you\"}" } });
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

    // demonstrate Java API for defining decision.
    private DmnModel getDecision() throws Exception {
        if (dm == null) {
            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(CD_DEFINITION_ID)
                    .withDescription(
                            "Calculates discount for a given customer.")
                    .withItemDefinitions(
                            objFact.createItemDefinition()
                                    .withId("customerCategory")
                                    .withName("Customer Category")
                                    .withTypeDefinition("string"),
                            objFact.createItemDefinition()
                                    .withId("orderSize")
                                    .withName("Order Size")
                                    .withTypeDefinition("number"),
                            objFact.createItemDefinition()
                                    .withId("totalOrderSum")
                                    .withTypeDefinition("number"),
                            objFact.createItemDefinition()
                                    .withId("amountDueDate")
                                    .withTypeDefinition("date")                                    
                    );

            // build expressions
            LiteralExpression orderSizeSmall = objFact
                    .createLiteralExpression().withId("27002_dt_i2_ie_1")
                    .withText("<![CDATA[< 500]]>");
            LiteralExpression orderSizeLarge = objFact
                    .createLiteralExpression().withId("27002_dt_i2_ie_2")
                    .withText("<![CDATA[>= 500]]>");
            LiteralExpression customerCategoryOther = objFact.createLiteralExpression()
                    .withId("27002_dt_i1_ie_1")
                    .withText(
                            "<![CDATA[!= \"gold\"]]>");
            LiteralExpression customerCategoryGold = objFact.createLiteralExpression()
                    .withId("27002_dt_i1_ie_2")
                    .withText(
                            "<![CDATA[== \"gold\"]]>");
            LiteralExpression totalPrice = objFact.createLiteralExpression()
                    .withId("27002_dt_o1_od_1")
                    .withText(
                            "ordersize * price");
            LiteralExpression totalPriceDiscounted = objFact.createLiteralExpression()
                    .withId("27002_dt_o1_od_2")
                    .withText(
                            "(ordersize * price) * 0.9");
            LiteralExpression dueDate = objFact.createLiteralExpression()
                    .withId("27002_dt_o2_od_1")
                    .withText("amountDueDate");
            LiteralExpression dueDateExtended = objFact.createLiteralExpression()
                    .withId("27002_dt_o2_od_2")
                    .withText(
                            "addDate(amountDueDate,0,1,0)");

            // build decision table from expressions
            DecisionTable dt = objFact.createDecisionTable()
                    .withId("27002_dt")
                    .withHitPolicy(HitPolicy.FIRST)
                    .withInputs(
                            objFact.createDtInput()
                                    .withId("27002_dt_i1")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i1_ie")
                                                    .withDescription("Customer Category"))
                                    .withInputValues(
                                            customerCategoryOther,
                                            customerCategoryGold
                                    ),
                            objFact.createDtInput()
                                    .withId("27002_dt_i2")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i2_ie")
                                                    .withDescription(
                                                            "Order Size"))
                                    .withInputValues(
                                            orderSizeSmall,
                                            orderSizeLarge)
                    )
                    .withOutputs(
                            objFact.createDtOutput()
                                    .withId("27002_dt_o1")
                            // .withOutputDefinition(
                            // objFact.createLiteralExpression()
                            // .withId("27002_dt_o1_od"))
                                    .withOutputValues(
                                            totalPrice,
                                            totalPriceDiscounted
                            ),
                            objFact.createDtOutput()
                                    .withId("27002_dt_o2")
                            // .withOutputDefinition(
                            // objFact.createLiteralExpression()
                            // .withId("27002_dt_o2_od"))
                                    .withOutputValues(
                                            dueDate,
                                            dueDateExtended))
                    .withRules(
                            objFact.createDecisionRule().withInputEntries(
                                    customerCategoryOther, totalPrice, dueDate));

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
                    .withName("Determine Customer Discount")
                    .withDecisionTable(dt);

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
