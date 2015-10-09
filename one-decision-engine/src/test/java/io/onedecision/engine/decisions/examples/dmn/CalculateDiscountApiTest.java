package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.validators.DmnValidationErrors;

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
        objFact = new ObjectFactory();

        de = new InMemoryDecisionEngineImpl();
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
        de.getRepositoryService().createModelForTenant(getDmnModel());

        vars.clear();
        vars.put("customerCategory", customerCategory);
        vars.put("orderSize", orderSize);
        de.getRuntimeService().executeDecision(CD_DEFINITION_ID,
                CD_DECISION_ID, vars, TENANT_ID);
        Assert.assertNotNull(vars.get("totalOrderPrice"));
    }

    // demonstrate Java API for defining decision.
    private DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition customerCategoryDef = objFact.createItemDefinition()
                    .withId("customerCategory")
                    .withName("Customer Category")
                    .withTypeDefinition("string");
            ItemDefinition orderSizeDef = objFact.createItemDefinition()
                    .withId("orderSize")
                    .withName("Order Size")
                    .withTypeDefinition("number");
            ItemDefinition totalPriceDef = objFact.createItemDefinition()
                    .withId("totalOrderPrice")
                    .withTypeDefinition("number");
            
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
            LiteralExpression orderSizeSmall = objFact
                    .createLiteralExpression().withId("27002_dt_i2_ie_1")
                    .withText("< 500");
            LiteralExpression orderSizeLarge = objFact
                    .createLiteralExpression().withId("27002_dt_i2_ie_2")
                    .withText(">= 500");
            LiteralExpression customerCategoryOther = objFact.createLiteralExpression()
                    .withId("27002_dt_i1_ie_1")
                    .withText("!= \"gold\"");
            LiteralExpression customerCategoryGold = objFact.createLiteralExpression()
                    .withId("27002_dt_i1_ie_2")
                    .withText("== \"gold\"");
            LiteralExpression totalPrice = objFact.createLiteralExpression()
                    .withId("27002_dt_o1_od_1")
                    .withText("orderSize * totalOrderPrice");
            LiteralExpression totalPriceDiscounted = objFact.createLiteralExpression()
                    .withId("27002_dt_o1_od_2")
                    .withText("(orderSize * totalOrderPrice) * 0.9");

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
                                                    .withText(
                                                            "customerCategory")
                                                    .withDescription(
                                                            "Customer Category"))
                                    .withInputValues(
                                            customerCategoryOther,
                                            customerCategoryGold
                                    ),
                            objFact.createDtInput()
                                    .withId("27002_dt_i2")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i2_ie")
                                                    .withText("orderSize")
                                                    .withDescription(
                                                            "Order Size"))
                                    .withInputValues(
                                            orderSizeSmall,
                                            orderSizeLarge)
                    )
                    .withOutputs(
                            objFact.createDtOutput()
                                    .withId("27002_dt_o1")
                                    .withOutputDefinition(
                                            objFact.createDmnElementReference()
                                                .withHref("#"+totalPriceDef.getId()))
                                    .withOutputValues(
                                            totalPrice,
                                            totalPriceDiscounted))
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

            def.withDecision(d);

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

}
