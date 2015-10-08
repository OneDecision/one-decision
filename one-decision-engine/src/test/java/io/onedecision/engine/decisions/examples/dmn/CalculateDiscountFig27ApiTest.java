package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionEngine;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.impl.InMemoryDecisionEngineImpl;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
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
 * Tests the fluent API's ability to create the Calculate Discount example from
 * Fig 27 of spec (p66 in 1.0 beta 2 version).
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class CalculateDiscountFig27ApiTest implements ExamplesConstants {

    private static ObjectFactory objFact;

    private static DecisionEngine de;

    private String customer;
    private Number orderSize;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { 
                { "Business", "100" },
                { "Business", "5" }, 
                { "Private", "100" } 
        });
    }

    @BeforeClass
    public static void setUpClass() {
        objFact = new ObjectFactory();

        de = new InMemoryDecisionEngineImpl();
    }

    public CalculateDiscountFig27ApiTest(String customerCategory, String orderSize) {
        this.customer = customerCategory;
        this.orderSize = new BigDecimal(orderSize);
    }

    @Test
    public void testReadDecision() throws Exception {

    }

    @Test
    public void testCalculateDiscount() throws Exception {
        de.getRepositoryService().createModelForTenant(getDmnModel());

        vars.clear();
        vars.put("Customer", customer);
        vars.put("Ordersize", orderSize);
        de.getRuntimeService().executeDecision(FIG27_DEFINITION_ID,
                FIG27_DECISION_ID, vars, TENANT_ID);
        Assert.assertNotNull(vars.get("discount"));
    }

    // demonstrate Java API for defining decision.
    private DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition discount = objFact.createItemDefinition()
                    .withId("discount")
                    .withName("Discount")
                    .withTypeDefinition("string");
            
            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(FIG27_DEFINITION_ID)
                    .withDescription(
                            "Calculates a customer discount using rules embedding the input/output values directly.")
                    .withItemDefinitions(discount);

            // build expressions
            LiteralExpression orderSizeSmall = objFact
                    .createLiteralExpression().withText("< 10");
            LiteralExpression orderSizeLarge = objFact
                    .createLiteralExpression().withId("27002_dt_i2_ie_2")
                    .withText(">= 10");
            LiteralExpression customerBusiness = objFact.createLiteralExpression()
                    .withId("27002_dt_i1_ie_1")
                    .withText("\"Business\"");
            LiteralExpression customerPrivate = objFact
                    .createLiteralExpression()
                    .withId("27002_dt_i1_ie_2")
                    .withText("\"Private\"");
            LiteralExpression discountSmall = objFact.createLiteralExpression()
                    .withId("27002_dt_o1_od_1").withText("0.05");
            LiteralExpression discountMedium = objFact
                    .createLiteralExpression()
                    .withId("27002_dt_o1_od_2")
                    .withText("0.10");
            LiteralExpression discountLarge = objFact.createLiteralExpression()
                    .withId("27002_dt_o2_od_1").withText("0.15");

            // build decision table from expressions
            DecisionTable dt = objFact.createDecisionTable()
                    .withId("27002_dt")
                    .withHitPolicy(HitPolicy.UNIQUE)
                    .withPreferedOrientation(
                            DecisionTableOrientation.RULE_AS_COLUMN)
                    .withInputs(
                            objFact.createDtInput()
                                    .withId("27002_dt_i1")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i1_ie")
                                                    .withText("Customer"))
                                    .withInputValues(
                                            customerBusiness,
                                            customerPrivate
                                    ),
                            objFact.createDtInput()
                                    .withId("27002_dt_i2")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i2_ie")
                                                    .withText("Ordersize"))
                                    .withInputValues(
                                            orderSizeSmall,
                                            orderSizeLarge)
                    )
                    .withOutputs(
                            objFact.createDtOutput()
                                    .withId("27002_dt_o1")
                                    .withName("Discount")
                                    .withOutputDefinition(
                                            objFact.createDmnElementReference()
                                                    .withHref("#discount"))
                                    .withOutputValues(
                                            discountSmall,
                                            discountMedium,
                                            discountLarge)
                            )
                    .withRules(
                            objFact.createDecisionRule()
                                    .withInputEntry(customerBusiness,
                                            orderSizeSmall)
                                    .withOutputEntry(discountMedium),
                            objFact.createDecisionRule()
                                    .withInputEntry(customerBusiness,
                                            orderSizeLarge)
                                    .withOutputEntry(discountLarge),
                            objFact.createDecisionRule()
                                    .withInputEntry(customerPrivate)
                                    .withOutputEntry(discountSmall));

            Decision d = objFact.createDecision()
                    .withId(FIG27_DECISION_ID)
                    .withName("Discount")
                    .withDecisionTable(dt);

            def.withDecision(d);

            assertSerializationProduced(def);

            dm = new DmnModel(def, null, TENANT_ID);
        }
        return dm;
    }

    private void assertSerializationProduced(Definitions dm)
            throws IOException, FileNotFoundException {
        Assert.assertNotNull("Definitions produced must not be null", dm);

        File dmnFile = new File("target", FIG27_DEFINITION_ID + ".dmn");
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
