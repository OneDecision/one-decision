package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertTrue;
import io.onedecision.engine.decisions.api.DecisionConstants;
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
 * Fig 37 of spec (p66 in 1.1 RTF v7).
 * 
 * @author Tim Stephenson
 */
@RunWith(Parameterized.class)
public class CalculateDiscountFig37ApiTest implements ExamplesConstants {

    private static ObjectFactory objFact;

    private static DecisionEngine de;

    private String customer;
    private Number orderSize;
    private String delivery;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { { "Business", "100", "-" },
                { "Business", "5", "-" }, { "Private", "2", "sameday" },
                { "Private", "2", "slow" }, { "Government", "30", "-" } });
    }

    @BeforeClass
    public static void setUpClass() {
        objFact = new ObjectFactory();

        de = new InMemoryDecisionEngineImpl();
    }

    public CalculateDiscountFig37ApiTest(String customerCategory,
            String orderSize, String delivery) {
        this.customer = customerCategory;
        this.orderSize = new BigDecimal(orderSize);
        this.delivery = delivery;
    }

    @Test
    public void testReadDecision() throws Exception {

    }

    @Test
    public void testCalculateDiscount() throws Exception {
        de.getRepositoryService().createModelForTenant(getDmnModel());

        vars.clear();
        vars.put("Customer", customer);
        vars.put("OrderSize", orderSize);
        vars.put("Delivery", delivery);
        // de.getRuntimeService().executeDecision(FIG37_DEFINITION_ID,
        // FIG37_DECISION_ID, vars, TENANT_ID);
        // Assert.assertNotNull(vars.get("discount"));
    }

    // demonstrate Java API for defining decision.
    private DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition discount = objFact.createItemDefinition()
                    .withId("discount").withName("Discount")
                    .withTypeRef(DecisionConstants.FEEL_STRING);

            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(FIG37_DEFINITION_ID)
                    .withDescription(
                            "Calculates a customer discount using rules embedding the input/output values directly.")
                    .withItemDefinitions(discount);

            // build expressions
            // build decision table from expressions
            DecisionTable dt = objFact
                    .createDecisionTable()
                    .withId("27002_dt")
                    .withHitPolicy(HitPolicy.UNIQUE)
                    .withPreferredOrientation(
                            DecisionTableOrientation.RULE_AS_COLUMN)
                    .withInputs(
                            objFact.createInputClause()
                                    .withInputExpression("Customer")
                                    .withInputValues("Business", "Private",
                                            "Government"),
                            objFact.createInputClause()
                                    .withInputExpression("OrderSize")
                                    .withInputValues("<10", ">=10"),
                            objFact.createInputClause()
                                    .withInputExpression("Delivery")
                                    .withInputValues("sameday", "slow"))
                    .withOutputs(
                            objFact.createOutputClause()
                                    .withName("Discount")
                                    .withOutputValues("0", "0.05", "0.10",
                                            "0.15"))
                    .withRules(
                            objFact.createDecisionRule()
                                    .withInputEntry("Business", "< 10", "-")
                                    .withOutputEntry("0.10"),
                            objFact.createDecisionRule()
                                    .withInputEntry("Business", ">= 10", "-")
                                    .withOutputEntry("0.15"),
                            objFact.createDecisionRule()
                                    .withInputEntry("Private", "-", "sameday")
                                    .withOutputEntry("0.05"),
                            objFact.createDecisionRule()
                                    .withInputEntry("Private", "-", "slow")
                                    .withOutputEntry("0.05"),
                            objFact.createDecisionRule()
                                    .withInputEntry("Government", "-", "-")
                                    .withOutputEntry("0.15"));

            Decision d = objFact
                    .createDecision()
                    .withId(FIG37_DECISION_ID)
                    .withName("Discount")
                    .withInformationItem(
                            objFact.createInformationItem().withId("discount"))
                    .withDecisionTable(dt);

            def.withDecisions(d);

            assertSerializationProduced(def);

            dm = new DmnModel(def, null, TENANT_ID);
        }
        return dm;
    }

    private void assertSerializationProduced(Definitions dm)
            throws IOException, FileNotFoundException {
        Assert.assertNotNull("Definitions produced must not be null", dm);

        File dmnFile = new File("target", FIG37_DEFINITION_ID + ".dmn");
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
