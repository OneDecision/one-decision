package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.UnaryTests;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;
import io.onedecision.engine.test.DecisionRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
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

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    private static ObjectFactory objFact;

    private String customerCategory;
    private Number orderSize;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { { "silver", "100" } });
    }

    @BeforeClass
    public static void setUpClass() {
        objFact = decisionRule.getObjectFactory();
    }

    public CalculateDiscountApiTest(String customerCategory, String orderSize) {
        this.customerCategory = customerCategory;
        this.orderSize = new BigDecimal(orderSize);
    }

    @Test
    public void testReadDecision() throws Exception {

    }

    @Test
    public void testCalculateDiscount() throws Exception {
        decisionRule.getDecisionEngine().getRepositoryService()
                .createModelForTenant(getDmnModel());

        vars.clear();
        vars.put("customerCategory", customerCategory);
        vars.put("orderSize", orderSize);
        decisionRule
                .getDecisionEngine()
                .getRuntimeService()
                .executeDecision(CD_DEFINITION_ID,
                CD_DECISION_ID, vars, TENANT_ID);
        Assert.assertNotNull(vars.get("totalOrderPrice"));
    }

    // demonstrate Java API for defining decision.
    private DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition customerCategoryDef = objFact.createItemDefinition()
                    .withName("customerCategory")
                    .withLabel("Customer Category")
                    .withTypeRef(DecisionConstants.FEEL_STRING);
            ItemDefinition orderSizeDef = objFact.createItemDefinition()
                    .withName("orderSize")
                    .withLabel("Order Size")
                    .withTypeRef(DecisionConstants.FEEL_NUMBER);
            ItemDefinition totalPriceDef = objFact.createItemDefinition()
                    .withName("totalOrderPrice")
                    .withTypeRef(DecisionConstants.FEEL_NUMBER);
            
            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(CD_DEFINITION_ID)
                    .withDescription(
                            "Calculates discount for a given customer.")
                    .withItemDefinitions(
                            customerCategoryDef,
                            orderSizeDef,
                            totalPriceDef
                    );

            // build expressions
            UnaryTests orderSizeSmall = objFact.createUnaryTests()
            // .withId("27002_dt_i2_ie_1")
                    .withText("< 500");
            UnaryTests orderSizeLarge = objFact.createUnaryTests()
            // .withId("27002_dt_i2_ie_2")
                    .withText(">= 500");
            UnaryTests customerCategoryOther = objFact.createUnaryTests()
            // .withId("27002_dt_i1_ie_1")
                    .withText("!= \"gold\"");
            UnaryTests customerCategoryGold = objFact.createUnaryTests()
            // .withId("27002_dt_i1_ie_2")
                    .withText("== \"gold\"");
            LiteralExpression totalPrice = objFact.createLiteralExpression()
                    .withText("orderSize * totalOrderPrice");
            LiteralExpression totalPriceDiscounted = objFact
                    .createLiteralExpression()
                    .withText("(orderSize * totalOrderPrice) * 0.9");

            // build decision table from expressions
            DecisionTable dt = objFact.createDecisionTable()
                    // .withId("27002_dt")
                    .withHitPolicy(HitPolicy.FIRST)
                    .withInputs(
                            objFact.createInputClause()
                                    // .withId("27002_dt_i1")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i1_ie")
                                                    .withText(
                                                            "customerCategory")
                                                    .withDescription(
                                                            "Customer Category"))
                                    .withInputValues(
                                            objFact.createUnaryTests()
                                                    .withUnaryTests(
                                                            "!= \"gold\"",
                                                            "== \"gold\"")
                                    ),
                            objFact.createInputClause()
                                    // .withId("27002_dt_i2")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i2_ie")
                                                    .withText("orderSize")
                                                    .withDescription(
                                                            "Order Size"))
                                    .withInputValues(
                                            objFact.createUnaryTests()
                                                    .withUnaryTests("< 500",
                                                            ">= 500")))
                    .withOutputs(
                            objFact.createOutputClause()
                    // .withId("27002_dt_o1")
                                    .withName("totalOrderPrice"))
                    .withRules(
                            objFact.createDecisionRule()
                                    .withInputEntry(customerCategoryOther)
                                    .withOutputEntry(totalPrice),
                            objFact.createDecisionRule()
                                    .withInputEntry(customerCategoryGold,
                                            orderSizeSmall)
                                    .withOutputEntry(totalPriceDiscounted),
                            objFact.createDecisionRule()
                                    .withInputEntry(customerCategoryGold,
                                            orderSizeLarge)
                                    .withOutputEntry(totalPriceDiscounted));

            Decision d = objFact.createDecision()
                    .withId(CD_DECISION_ID)
                    .withName("Determine Customer Discount")
                    .withInformationItem(
                            objFact.createInformationItem().withId(
                                    "totalOrderPrice"))
                    .withDecisionTable(dt);

            def.withDecisions(d);

            assertSerializationProduced(def);

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
        Assert.assertNotNull("Definitions produced must not be null", dm);

        File dmnFile = new File(DecisionRule.outputDir, CD_DEFINITION_ID + ".dmn");
        FileWriter out = new FileWriter(dmnFile);
        try {
            decisionRule.getDecisionEngine().getRepositoryService()
                    .write(dm, out);
        } finally {
            out.close();
        }
        System.out.println("Wrote dmn to: " + dmnFile);
        assertTrue(dmnFile.exists());

        InputStream fis = null;
        try {
            fis = new FileInputStream(dmnFile);
            DmnValidationErrors errors = new DmnValidationErrors(
                    dmnFile.getName());
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

}
