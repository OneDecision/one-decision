package io.onedecision.engine.decisions.examples.dmn;

import static org.junit.Assert.assertNotNull;
import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.examples.ExamplesConstants;
import io.onedecision.engine.decisions.model.dmn.BuiltInAggregator;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.InputData;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.test.DecisionRule;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class SuiteATest implements ExamplesConstants {

    private static final String A_DECISIONS_URI = "http://onedecision.io/examples/";
    private static final String A_1_DEFINITION_ID = "A.1";
    private static final String A_2_DEFINITION_ID = "A.2";

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
        // decisionRule.validateSchema("A.1.dmn");
        decisionRule.validate(def);
    }

    @Test
    public void testCollectingDT() throws IOException {
        Definitions def = getCalcPriceDecisionModelUsingCollectingDT();
        assertNotNull(def);

        decisionRule.writeDmn(def, "A.2.dmn");
        // decisionRule.validateSchema("A.2.dmn");
        decisionRule.validate(def);
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
                        objFact.createDecisionRule().withInputEntry("<2")
                                .withInputEntry("false").withOutputEntry("0"),
                        objFact.createDecisionRule().withInputEntry("<2")
                                .withInputEntry("true").withOutputEntry("10"),
                        objFact.createDecisionRule().withInputEntry("[3-16]")
                                .withInputEntry("false").withOutputEntry("20"),
                        objFact.createDecisionRule().withInputEntry("[3-16]")
                                .withInputEntry("true").withOutputEntry("30"),
                        objFact.createDecisionRule().withInputEntry(">=16")
                                .withInputEntry("false").withOutputEntry("50"),
                        objFact.createDecisionRule().withInputEntry(">=16")
                                .withInputEntry("true").withOutputEntry("60"));

        Definitions def = objFact.createDefinitions()
                .withId(A_1_DEFINITION_ID)
                .withName("Calculate Price Decision Model")
                .withDescription("Implements the pricing model")
                .withNamespace(A_DECISIONS_URI)
                .withInputData(getAgeInputData(), getPrioityInputData())
                .withDecisions(
                        objFact.createDecision()
                                .withId("calcPrice_d")
                                .withName("Calculate Price Decision")
                                .withDescription(
                                        "Determine price based on age of applicant and whether priority service requested")
                                .withDecisionTable(calcPriceDT));

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
                .withDescription("Whether priorty service was requested")
                .withTypeRef(DecisionConstants.FEEL_BOOLEAN);
        return priorityII;
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
                        objFact.createDecisionRule().withInputEntry("<2")
                                .withInputEntry("-").withOutputEntry("0"),
                        objFact.createDecisionRule().withInputEntry("[3-16]")
                                .withInputEntry("-").withOutputEntry("20"),
                        objFact.createDecisionRule().withInputEntry(">=16")
                                .withInputEntry("-").withOutputEntry("50"),
                        objFact.createDecisionRule().withInputEntry("-")
                                .withInputEntry("true").withOutputEntry("10"));

        Definitions def = objFact
                .createDefinitions()
                .withId(A_2_DEFINITION_ID)
                .withName("Calculate Price Decision Model")
                .withDescription("Implements the pricing model")
                .withNamespace(A_DECISIONS_URI)
                .withInputData(getAgeInputData(), getPrioityInputData())
                .withDecisions(
                        objFact.createDecision()
                                .withId("calcPrice_d")
                                .withName("Calculate Price Decision")
                                .withDescription(
                                        "Determine price based on age of applicant and whether priority service requested")
                                .withDecisionTable(calcPriceDT));

        return def;
    }
}
