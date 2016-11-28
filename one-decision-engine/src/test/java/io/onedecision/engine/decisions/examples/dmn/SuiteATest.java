package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.BuiltInAggregator;
import io.onedecision.engine.decisions.model.dmn.Context;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.InputData;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.test.DecisionRule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class SuiteATest implements ExamplesConstants {

    protected static final String CRLF = System.getProperty("line.separator");

    private static final String A_DECISIONS_URI = "http://onedecision.io/examples/";
    private static final String A_1_DEFINITION_ID = "A.1";
    private static final String A_1_DECISION_ID = "calcPrice_d";
    private static final String A_2_DEFINITION_ID = "A.2";
    private static final String A_2_DECISION_ID = "calcPrice_d";
    private static final String A_3_DEFINITION_ID = "A.3";
    private static final String A_3_DECISION_ID = "calcPrice_d";

    @ClassRule
    public static DecisionRule decisionRule = new DecisionRule();

    private static ObjectFactory objFact;

    @BeforeClass
    public static void setUpClass() {
        DecisionRule.outputDir.mkdirs();
        objFact = decisionRule.getObjectFactory();
    }

    @Test
    public void testUniqueDT() throws IOException {
        Definitions def = getCalcPriceDecisionModelUsingUniqueDT();
        assertNotNull(def);

        decisionRule.writeDmn(def, "A.1.dmn");
        assertEquals(0, decisionRule.validate(def).size());

        DmnModel model = new DmnModel(def, TENANT_ID);
        decisionRule.getDecisionEngine().getRepositoryService()
                .createModelForTenant(model);

        execute("1", false, 0.0);
        execute("1", true, 10.0);
        execute("5", false, 20.0);
        execute("10", true, 30.0);
        execute("16", false, 40.0);
        execute("16", true, 50.0);
    }

    private void execute(String age, boolean priority, double expectedPrice) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("age", age);
        params.put("priority", priority);
        Map<String, Object> response = decisionRule
                .getDecisionEngine()
                .getRuntimeService()
                .executeDecision(A_1_DEFINITION_ID, A_1_DECISION_ID, params,
                        TENANT_ID);
        assertNotNull(response);
        System.out.println("Response" + response);
        assertEquals(1, response.size());
        assertEquals(expectedPrice,
                ((Number) response.get("price")).doubleValue(), 0.1);
    }

    @Test
    public void testCollectingDT() throws IOException {
        Definitions def = getCalcPriceDecisionModelUsingCollectingDT();
        assertNotNull(def);

        decisionRule.writeDmn(def, "A.2.dmn");
        assertEquals(0, decisionRule.validate(def).size());
    }

    private Definitions getCalcPriceDecisionModelUsingUniqueDT() {
        DecisionTable calcPriceDT = objFact
                .createDecisionTable()
                // Unclear why would need description on DT as well D?
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause().withInputExpression("age"),
                        objFact.createInputClause().withInputExpression(
                                "priority"))
                .withOutputs(objFact.createOutputClause().withName("price"))
                .withRules(
                        objFact.createDecisionRule().withInputEntries("<2")
                                .withInputEntries("false")
                                .withOutputEntries("0"),
                        objFact.createDecisionRule().withInputEntries("<2")
                                .withInputEntries("true").withOutputEntries("10"),
                        objFact.createDecisionRule()
                                .withInputEntries("[3..16]")
                                .withInputEntries("false").withOutputEntries("20"),
                        objFact.createDecisionRule()
                                .withInputEntries("[3..16]")
                                .withInputEntries("true").withOutputEntries("30"),
                        objFact.createDecisionRule().withInputEntries(">=16")
                                .withInputEntries("false")
                                .withOutputEntries("40"),
                        objFact.createDecisionRule().withInputEntries(">=16")
                                .withInputEntries("true")
                                .withOutputEntries("50"));

        Definitions def = objFact.createDefinitions()
                .withId(A_1_DEFINITION_ID)
                .withName("Calculate Price Decision Model")
                .withDescription("Implements the pricing model")
                .withNamespace(A_DECISIONS_URI)
                .withInputData(getAgeInputData(), getPrioityInputData())
                .withDecisions(
                        objFact.createDecision()
                                .withId(A_1_DECISION_ID)
                                .withName("Calculate Price Decision")
                                .withDescription(
                                        "Determine price based on age of applicant and whether priority service requested")
                                .withVariable(getPriceInformationItem())
                                .withDecisionTable(calcPriceDT));

        return def;
    }

    @Test
    public void testBoxedContext() throws IOException {
        Definitions def = getCalcPriceDecisionModelUsingBoxedContext();
        assertNotNull(def);

        decisionRule.writeDmn(def, "A.3.dmn");
        assertEquals(0, decisionRule.validate(def).size());
    }

    private Definitions getCalcPriceDecisionModelUsingBoxedContext() {
        StringBuilder priceExpr = new StringBuilder()
                .append("if age < 2 and priority = false then 0")
                .append(CRLF)
                .append("else if age < 2 and priority = true then 10.00")
                .append(CRLF)
                .append("else if age in ([3-16]) and priority = false then 20.00")
                .append(CRLF)
                .append("else if age in ([3-16]) and priority = true then 30.00")
                .append(CRLF).append("else if priority = false then 40.00")
                .append(CRLF).append("else 50.00");

        Context calcPriceBC = objFact
                .createContext()
                .withContextEntry(
                        objFact.createContextEntry()
                                .withVariable(getAgeInformationItem())
                                .withLiteralExpression("age"))
                .withContextEntry(
                        objFact.createContextEntry()
                                .withVariable(getPriorityInformationItem())
                                .withLiteralExpression("priority"))
                .withContextEntry(
                        objFact.createContextEntry()
                                .withVariable(getPriceInformationItem())
                                .withLiteralExpression(priceExpr.toString()));

        Definitions def = objFact
                .createDefinitions()
                .withId(A_3_DEFINITION_ID)
                .withName("Calculate Price Decision Model")
                .withDescription("Implements the pricing model")
                .withNamespace(A_DECISIONS_URI)
                .withInputData(getAgeInputData(), getPrioityInputData())
                .withDecisions(
                        objFact.createDecision()
                                .withId(A_3_DECISION_ID)
                                .withName("Calculate Price Decision")
                                .withDescription(
                                        "Determine price based on age of applicant and whether priority service requested")
                                .withVariable(getPriceInformationItem())
                                .withContext(calcPriceBC));

        return def;
    }

    private InputData getPrioityInputData() {
        return objFact.createInputData().withName("priority")
                .withVariable(getPriorityInformationItem());
    }

    private InputData getAgeInputData() {
        return objFact.createInputData().withName("age")
                .withVariable(getAgeInformationItem());
    }

    private InformationItem getAgeInformationItem() {
        return objFact.createInformationItem()
                .withName("age")
                .withDescription("The age of the applicant")
                .withTypeRef(DecisionConstants.FEEL_NUMBER);
    }

    private InformationItem getPriorityInformationItem() {
        InformationItem priorityII = objFact.createInformationItem()
                .withName("priority")
                .withDescription("Whether priority service was requested")
                .withTypeRef(DecisionConstants.FEEL_BOOLEAN);
        return priorityII;
    }

    private InformationItem getPriceInformationItem() {
        InformationItem priceII = objFact.createInformationItem()
                .withName("price").withDescription("Price to charge customer")
                .withTypeRef(DecisionConstants.FEEL_NUMBER);
        return priceII;
    }

    private Definitions getCalcPriceDecisionModelUsingCollectingDT() {
        DecisionTable calcPriceDT = objFact
                .createDecisionTable()
                // Unclear why would need description on DT as well D?
                .withHitPolicy(HitPolicy.COLLECT)
                .withAggregation(BuiltInAggregator.SUM)
                .withPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause().withInputExpression("age"),
                        objFact.createInputClause().withInputExpression(
                                "priority"))
                .withOutputs(objFact.createOutputClause().withName("price"))
                .withRules(
                        objFact.createDecisionRule().withInputEntries("<2")
                                .withInputEntries("-").withOutputEntries("0"),
                        objFact.createDecisionRule()
                                .withInputEntries("[3..16]")
                                .withInputEntries("-").withOutputEntries("20"),
                        objFact.createDecisionRule().withInputEntries(">=16")
                                .withInputEntries("-").withOutputEntries("40"),
                        objFact.createDecisionRule().withInputEntries("-")
                                .withInputEntries("true").withOutputEntries("10"));

        Definitions def = objFact
                .createDefinitions()
                .withId(A_2_DEFINITION_ID)
                .withName("Calculate Price Decision Model")
                .withDescription("Implements the pricing model")
                .withNamespace(A_DECISIONS_URI)
                .withInputData(getAgeInputData(), getPrioityInputData())
                .withDecisions(
                        objFact.createDecision()
                                .withId(A_2_DECISION_ID)
                                .withName("Calculate Price Decision")
                                .withDescription(
                                        "Determine price based on age of applicant and whether priority service requested")
                                .withDecisionTable(calcPriceDT));

        return def;
    }
}
