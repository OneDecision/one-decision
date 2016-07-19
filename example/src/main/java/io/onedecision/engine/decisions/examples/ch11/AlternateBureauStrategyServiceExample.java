package io.onedecision.engine.decisions.examples.ch11;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.converter.DecisionModelConverter;
import io.onedecision.engine.decisions.model.dmn.BuiltInAggregator;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.InputData;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.KnowledgeSource;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.UnaryTests;
import io.onedecision.engine.test.MockDomainModelFactory;
import io.onedecision.engine.test.TestHelper;

import javax.xml.namespace.QName;

/**
 * An alternate implementation of the diagram in Figure 74: Bureau Strategy
 * Decision Service of the specification.
 * 
 * <p>
 * This implementation is a simplification of the one in the specification in
 * two ways:
 * <ul>
 * <li>The decision logic is simpler, relying on a two-pronged strategy of risk
 * assessment and affordability.
 * <li>The implementation prefers to rely only on the simplest constructs of
 * Decision and Decision Table rather than BKMs and so forth.
 *
 * @author Tim Stephenson
 */
public class AlternateBureauStrategyServiceExample implements DecisionConstants, ExamplesConstants {

    public static String LO_URI = "http://onedecision.io/examples/AlternateLoanOrigination";

    public static final QName APPLICANT_DATA = new QName(LO_URI,
            "ApplicantData", "ex");

    public static final QName BUREAU_DATA = new QName(LO_URI,
            "BureauData", "ex");

    public static final QName REQUESTED_PRODUCT = new QName(LO_URI,
            "RequestedProduct", "ex");

    public static final QName RISK_CATEGORY = new QName(LO_URI,
            "RiskCategory", "ex");

    public static final QName SUPPORTING_DOCUMENTS = new QName(
            LO_URI, "SupportingDocs", "ex");

    private static ObjectFactory objFact;

    protected DecisionModelConverter converter;

    protected UnaryTests emptyTest;
    protected UnaryTests falseTest;
    protected UnaryTests trueTest;

    private DmnModel dm;

    AlternateBureauStrategyServiceExample() throws Exception {
        objFact = new ObjectFactory();
        emptyTest = objFact.createUnaryTests().withText("-");
        falseTest = objFact.createUnaryTests().withText("false");
        trueTest = objFact.createUnaryTests().withText("true");

        converter = new DecisionModelConverter();
        converter.setDomainModelFactory(new MockDomainModelFactory(
                "http://onedecision.io/loans", "/domains/loans.json"));
    }

    // demonstrate Java API for defining decision.
    protected DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition applicantDataDef = objFact.createItemDefinition()
                    .withId("applicantData_t")
                    .withName("Applicant data")
//                    .withTypeRef(APPLICANT_DATA)
                    .withItemComponent(
                            objFact.createItemDefinition()
                            .withId("age_t")
                            .withName("Age")
                            .withTypeRef(FEEL_NUMBER))
                    .withItemComponent(objFact.createItemDefinition()
                            .withId("maritalStatus_t")
                            .withName("MaritalStatus")
                            .withTypeRef(FEEL_STRING)
                            .withAllowedValues(
                                    objFact.createUnaryTests()
                                            .withUnaryTests("S","M")))
                    .withItemComponent(objFact.createItemDefinition()
                            .withId("employmentStatus_t")
                            .withName("EmploymentStatus")
                            .withTypeRef(FEEL_STRING))
                    .withItemComponent(objFact.createItemDefinition()
                            .withId("existingCustomer_t")
                            .withName("ExistingCustomer")
                            .withTypeRef(FEEL_BOOLEAN))
                    .withItemComponent(
                            objFact.createItemDefinition().withId("monthly")
                            .withName("Monthly")
                            .withItemComponent(
                                    objFact.createItemDefinition()
                                        .withId("income_t")
                                        .withName("Income")
                                        .withTypeRef(FEEL_NUMBER),
                                    objFact.createItemDefinition()
                                        .withId("repayments_t")
                                        .withName("Repayments")
                                        .withTypeRef(FEEL_NUMBER),
                                    objFact.createItemDefinition()
                                        .withId("expenses_t")
                                        .withName("Expenses")
                                        .withTypeRef(FEEL_NUMBER)));
            
            ItemDefinition riskScoreDef = objFact.createItemDefinition()
                    .withId("riskScore_t").withName("Risk Score")
                    .withTypeRef(FEEL_NUMBER);
            
            ItemDefinition affordabilityDef = objFact
                    .createItemDefinition()
                    .withId("affordablity_t")
                    .withName("Afforability")
                    .withAllowedValues(
                            objFact.createUnaryTests().withUnaryTests("YES",
                                    "MAYBE", "NO"))
                    .withTypeRef(FEEL_STRING);
            
            ItemDefinition strategyDef = objFact.createItemDefinition()
                    .withId("strategy_t")
                    .withName("Stategy")
                    .withAllowedValues(objFact.createUnaryTests()
                            .withUnaryTests("BUREAU", "DECLINE", "THROUGH"))
                    .withTypeRef(FEEL_STRING);

            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(LO_DEFINITION_ID)
                    .withName("Alternate Loan Origination Decision Model")
                    .withDescription(
                            "A simplified way to model the decision logic from chapter 11 of DMN 1.1 spec")
                    .withNamespace(LO_URI)
                    .withItemDefinitions(applicantDataDef, affordabilityDef,
                            riskScoreDef, strategyDef);

            // build input data
            InformationItem applicantDataItem = objFact.createInformationItem()
                    .withId("applicantId_ii")
                    .withName("Applicant data")
                    .withTypeRef(APPLICANT_DATA);
            InputData applicantData = objFact.createInputData()
                    .withId("applicantData_id")
                    .withName("Applicant data")
                    .withVariable(applicantDataItem);
            InformationItem requestedProductItem = objFact.createInformationItem()
                    .withId("requestedProduct_ii")
                    .withName("Requested product")
                    .withTypeRef(REQUESTED_PRODUCT);
            InputData requestedProduct = objFact.createInputData()
                    .withId("requestedProduct_id")
                    .withName("Requested product")
                    .withVariable(requestedProductItem);
            InformationItem supportingDocumentsItem = objFact.createInformationItem()
                    .withId("supportingDocuments_ii")
                    .withName("Supporting documents")
                    .withTypeRef(SUPPORTING_DOCUMENTS);
            InputData supportingDocuments = objFact.createInputData()
                    .withId("supportingDocuments_id")
                    .withName("Supporting documents")
                    .withVariable(supportingDocumentsItem);
            InformationItem bureauDataItem = objFact.createInformationItem()
                    .withId("bureauData_ii")
                    .withName("Bureau data")
                    .withTypeRef(BUREAU_DATA);
            InputData bureauData = objFact.createInputData()
                    .withId("bureauData_id")
                    .withName("Bureau data")
                    .withVariable(bureauDataItem);
            def.withInputData(applicantData, requestedProduct,
                    supportingDocuments, bureauData);

            // build knowledge sources
            KnowledgeSource riskManagerKS = objFact.createKnowledgeSource()
                    .withId("riskManager_ks")
                    .withName("Risk Manager");
            KnowledgeSource analyticsKS = objFact.createKnowledgeSource()
                    .withId("analytics_ks")
                    .withName("Analytics");;
            KnowledgeSource productSpecKS = objFact.createKnowledgeSource()
                    .withId("productSpecification_ks")
                    .withName("Product specification");
            KnowledgeSource creditOfficerKS = objFact.createKnowledgeSource()
                    .withId("creditOfficer_ks")
                    .withName("Credit officer");
            KnowledgeSource affordabilityKS = objFact.createKnowledgeSource()
                    .withId("affordabilitySpreadsheet_ks")
                    .withName("Affordability Spreadsheet");
            def.withKnowledgeSources(riskManagerKS, analyticsKS, productSpecKS,
                    creditOfficerKS, affordabilityKS);

            Decision applicationRiskScoreD = objFact.createDecision()
                    .withId("applicationRiskScore_d")
                    .withName("Application Risk Score")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredInput(applicantData))
                    .withDecisionTable(getApplicationRiskScoreDT());

            Decision preBureauAffordabilityD = objFact.createDecision()
                    .withId("preBureauAffordability_d")
                    .withName("Pre-bureau affordability")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredInput(applicantData))
                    .withDecisionTable(getAffordabilityDT());

            Decision strategyD = objFact.createDecision()
                    .withId("strategy_d")
                    .withName("Strategy")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(applicationRiskScoreD),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            preBureauAffordabilityD))
                    .withDecisionTable(getStrategyDT());
            


            def.withDecisions(applicationRiskScoreD, strategyD,
                    preBureauAffordabilityD);

            objFact.createDecisionService().withId("bureauStrategy_ds")
                    .withName("Bureau Strategy Service")
                    .withOutputDecisions(strategyD)
                    .withInputData(applicantData, requestedProduct);

            TestHelper.assertSerializationProduced(def);

            dm = new DmnModel(def, null, TENANT_ID);
        }

        return dm;
    }

    protected DecisionTable getApplicationRiskScoreDT() {
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("applicationRiskScore_dt")
                .withHitPolicy(HitPolicy.COLLECT)
                .withAggregation(BuiltInAggregator.SUM)
                .withPreferredOrientation(
                        DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Age"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withText("[18..120]")),
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Marital Status"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("S", "M")),
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Employment Status"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("UNEMPLOYED",
                                                        "EMPLOYED",
                                                        "SELF-EMPLOYED",
                                                        "STUDENT")))
                .withOutputs(
                        objFact.createOutputClause()
                                .withName("Partial score"))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "[18..21]"), emptyTest,
                                        emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("32")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "[22..25]"), emptyTest,
                                        emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("35")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "[26..35]"), emptyTest,
                                        emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("40")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "[36..49]"), emptyTest,
                                        emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("43")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                ">=50"), emptyTest, emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("48")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("S"), emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("25")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("M"), emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("45")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("UNEMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("15")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("STUDENT"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("18")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("EMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("45")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests()
                                                .withUnaryTests("SELF-EMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("36")));
        return dt;
    }


    private DecisionTable getAffordabilityDT() {
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("affodability_dt")
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferredOrientation(
                        DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Disposable Income")))
                .withOutputs(
                        objFact.createOutputClause().withName("Affordable"))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText("DisposableIncome * 0.6 >= PMT(Rate, Term, Amount)"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression().withText("YES")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText("DisposableIncome * 0.8 >= PMT(Rate, Term, Amount)"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression().withText("MAYBE")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText("DisposableIncome * 0.8 < PMT(Rate, Term, Amount)"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression().withText("NO")));
        return dt;
    }
    
    private DecisionTable getStrategyDT() {
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("strategy_dt")
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferredOrientation(
                        DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Application Risk Score"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withText("[0..150]")),
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Affordable"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("YES", "MAYBE", "NO")))
                .withOutputs(
                        objFact.createOutputClause()
                                .withName("Strategy"))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(">=120"),
                                        objFact.createUnaryTests().withText("YES"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("THROUGH")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText("<120"),
                                        objFact.createUnaryTests().withText("YES"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("BUREAU")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(">=100"),
                                        objFact.createUnaryTests().withText("MAYBE"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("BUREAU")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText("<100"),
                                        objFact.createUnaryTests().withText("MAYBE"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")) ,
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        objFact.createUnaryTests().withText("NO"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")));   
        return dt;
    }

}
