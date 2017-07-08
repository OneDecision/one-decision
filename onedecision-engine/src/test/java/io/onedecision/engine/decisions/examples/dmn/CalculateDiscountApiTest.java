package io.onedecision.engine.decisions.examples.dmn;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.UnaryTests;
import io.onedecision.engine.test.DecisionRule;

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
    private Number itemPrice;
    private DmnModel dm;
    private Map<String, Object> vars = new HashMap<String, Object>();

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] { { "silver", "100", "1" } });
    }

    @BeforeClass
    public static void setUpClass() {
        objFact = decisionRule.getObjectFactory();
    }

    public CalculateDiscountApiTest(String customerCategory, String orderSize,
            String itemPrice) {
        this.customerCategory = customerCategory;
        this.orderSize = new BigDecimal(orderSize);
        this.itemPrice = new BigDecimal(itemPrice);
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
        vars.put("itemPrice", itemPrice);
        vars.put("orderSize", orderSize);
        decisionRule
                .getDecisionEngine()
                .getRuntimeService()
                .executeDecision(CD_DEFINITION_ID, CD_DECISION_ID, vars,
                        TENANT_ID);
        System.out.println("  " + vars);
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
                    .withName("orderSize").withLabel("Order Size")
                    .withTypeRef(DecisionConstants.FEEL_NUMBER);
            ItemDefinition itemPriceDef = objFact.createItemDefinition()
                    .withName("itemPrice").withLabel("Item Price")
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
                    .withItemDefinitions(customerCategoryDef, orderSizeDef,
                            totalPriceDef);

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
                    .withText("orderSize * itemPrice");
            LiteralExpression totalPriceDiscounted = objFact
                    .createLiteralExpression().withText(
                            "(orderSize * itemPrice) * 0.9");

            // build decision table from expressions
            DecisionTable dt = objFact
                    .createDecisionTable()
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
                                                            "== \"gold\"")),
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
                                                            ">= 500")),
                            objFact.createInputClause()
                                    // .withId("27002_dt_i2")
                                    .withInputExpression(
                                            objFact.createLiteralExpression()
                                                    .withId("27002_dt_i3_ie")
                                                    .withText("itemPrice")
                                                    .withDescription(
                                                            "Item Price"))
                                    .withInputValues(
                                            objFact.createUnaryTests()
                                                    .withUnaryTests("1")))
                    .withOutputs(objFact.createOutputClause()
                    // .withId("27002_dt_o1")
                            .withName("totalOrderPrice"))
                    .withRules(
                            objFact.createDecisionRule()
                                    .withInputEntries(customerCategoryOther)
                                    .withOutputEntry(totalPrice),
                            objFact.createDecisionRule()
                                    .withInputEntries(customerCategoryGold,
                                            orderSizeSmall)
                                    .withOutputEntry(totalPriceDiscounted),
                            objFact.createDecisionRule()
                                    .withInputEntries(customerCategoryGold,
                                            orderSizeLarge)
                                    .withOutputEntry(totalPriceDiscounted));

            Decision d = objFact
                    .createDecision()
                    .withId(CD_DECISION_ID)
                    .withName("Determine Customer Discount")
                    .withInformationItem(
                            objFact.createInformationItem().withId(
                                    "totalOrderPrice"))
                    .withVariable(getTotalOrderPriceInformationItem())
                    .withDecisionTable(dt);

            def.withDecisions(d);

            decisionRule.writeDmn(def, def.getId() + ".dmn");
            decisionRule.validate(def);

            dm = new DmnModel(def, null, TENANT_ID);
        }
        return dm;
    }

    private InformationItem getTotalOrderPriceInformationItem() {
        InformationItem priceII = objFact.createInformationItem()
                .withName("totalOrderPrice")
                .withDescription("Total price of order")
                .withTypeRef(DecisionConstants.FEEL_NUMBER);
        return priceII;
    }

}
