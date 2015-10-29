package io.onedecision.engine.decisions.examples.ch11;

import io.onedecision.engine.decisions.converter.DecisionModelConverter;
import io.onedecision.engine.decisions.model.dmn.BusinessKnowledgeModel;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InputData;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.KnowledgeSource;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.ui.DecisionModel;
import io.onedecision.engine.test.MockDomainModelFactory;
import io.onedecision.engine.test.TestHelper;

import java.io.IOException;

public class Ch11LoanExample implements ExamplesConstants {

    private static ObjectFactory objFact;

    protected DecisionModelConverter converter;

    protected LiteralExpression emptyLE;
    protected LiteralExpression falseLE;
    protected LiteralExpression trueLE;

    private DmnModel dm;

    public Ch11LoanExample() throws Exception {
        objFact = new ObjectFactory();
        emptyLE = objFact.createLiteralExpression().withText("-");
        falseLE = objFact.createLiteralExpression().withText("false");
        trueLE = objFact.createLiteralExpression().withText("true");

        converter = new DecisionModelConverter();
        converter.setDomainModelFactory(new MockDomainModelFactory(
                "http://onedecision.io/loans", "/domains/loans.json"));
    }

    // demonstrate Java API for defining decision.
    protected DmnModel getDmnModel() throws Exception {
        if (dm == null) {
            // build item definitions
            ItemDefinition applicantDataDef = objFact.createItemDefinition()
                    .withId("applicantData").withName("Applicant Data")
                    .withTypeDefinition("string"); // TODO
            ItemDefinition bureauDataDef = objFact.createItemDefinition()
                    .withId("bureauData").withName("Bureau Data")
                    .withTypeDefinition("string"); // TODO

            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(CH11_DEFINITION_ID)
                    .withDescription("Implements model from chapter 11 of spec")
                    .withItemDefinitions(applicantDataDef, bureauDataDef);

            // build input data
            InputData applicantData = objFact.createInputData().withId(
                    "Applicant data");
            InputData requestedProduct = objFact.createInputData().withId(
                    "Requested product");
            InputData supportingDocuments = objFact.createInputData().withId(
                    "Supporting documents");
            InputData bureauData = objFact.createInputData().withId(
                    "Bureau data");
            def.withInputData(applicantData, requestedProduct,
                    supportingDocuments, bureauData);

            // build knowledge sources
            KnowledgeSource riskManagerKS = objFact.createKnowledgeSource()
                    .withId("Risk Manager");
            KnowledgeSource analyticsKS = objFact.createKnowledgeSource()
                    .withId("Analytics");
            KnowledgeSource productSpecKS = objFact.createKnowledgeSource()
                    .withId("Product specification");
            KnowledgeSource creditOfficerKS = objFact.createKnowledgeSource()
                    .withId("Credit officer");
            KnowledgeSource affordabilityKS = objFact.createKnowledgeSource()
                    .withId("Affordability Spreadsheet");
            def.withKnowledgeSources(riskManagerKS, analyticsKS, productSpecKS,
                    creditOfficerKS, affordabilityKS);

            // build business knowledge model
            BusinessKnowledgeModel bureauCallTypeTable = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Bureau call type table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            riskManagerKS
                                                                    .getId())));
            BusinessKnowledgeModel preBureauRiskCategoryTable = objFact
                    .createBusinessKnowledgeModel().withId(
                            "Pre-bureau risk category table");
            BusinessKnowledgeModel applicationRiskScoreModel = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Application risk score model")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            analyticsKS.getId())));
            BusinessKnowledgeModel creditContingencyFactorTable = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Credit contingency factor table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            riskManagerKS
                                                                    .getId())));
            BusinessKnowledgeModel affordabilityCalculation = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Affordability calculation")
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            creditContingencyFactorTable
                                                                    .getId())))
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            affordabilityKS
                                                                    .getId())));
            BusinessKnowledgeModel eligibilityRules = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Eligibility rules")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            productSpecKS
                                                                    .getId())));
            BusinessKnowledgeModel routingRules = objFact
                    .createBusinessKnowledgeModel().withId("Routing rules");
            BusinessKnowledgeModel installmentCalculation = objFact
                    .createBusinessKnowledgeModel().withId(
                            "Installment calculation");
            BusinessKnowledgeModel postBureauRiskCategoryTable = objFact
                    .createBusinessKnowledgeModel()
                    .withId("Post-bureau risk category table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            riskManagerKS
                                                                    .getId())));

            def.withBusinessKnowledgeModel(bureauCallTypeTable,
                    preBureauRiskCategoryTable, applicationRiskScoreModel,
                    creditContingencyFactorTable, eligibilityRules,
                    routingRules, installmentCalculation,
                    postBureauRiskCategoryTable);

            // build decisions
            Decision strategyDecision = objFact.createDecision()
                    .withId("Strategy")
                    .withDecisionTable(getStrategyDecisionTable());

            Decision eligibilityDecision = objFact
                    .createDecision()
                    .withId("Eligibility")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            strategyDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            eligibilityRules
                                                                    .getId())))
                    .withDecisionTable(getEligibilityDecisionTable());
            Decision bureauCallTypeDecision = objFact
                    .createDecision()
                    .withId("Bureau call type")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            strategyDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            bureauCallTypeTable
                                                                    .getId())))
//                    .withExpression(getBureauCallTypeDecision())
                    ;
            Decision preBureauAffordabilityDecision = objFact
                    .createDecision()
                    .withId("Pre-bureau affordability")
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            affordabilityCalculation
                                                                    .getId())))
//                    .withExpression(getPreBureauAffordibilityDecision())
                    ;
            
            // TODO with applicant data
            Decision preBureauRiskCategoryDecision = objFact
                    .createDecision()
                    .withId("Pre-bureau risk category")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            eligibilityDecision
                                                                    .getId())),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            bureauCallTypeDecision
                                                                    .getId())),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            preBureauAffordabilityDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            preBureauRiskCategoryTable
                                                                    .getId())))
            // .withInformationItem(objFact.createInformationItem().with);
            // TODO How to bind input data 'applicant data'?
                    .withDecisionTable(getPreBureauRiskCategoryDecision());

            preBureauAffordabilityDecision.withInformationRequirements(objFact
                    .createInformationRequirement().withRequiredDecision(
                            objFact.createDmnElementReference().withHref(
                                    preBureauRiskCategoryDecision.getId())));
            Decision applicationRiskScoreDecision = objFact
                    .createDecision()
                    .withId("Application risk score")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            preBureauRiskCategoryDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            applicationRiskScoreModel
                                                                    .getId())))
                    .withDecisionTable(getApplicationRiskScoreDecisionTable());
            Decision adjudicationDecision = objFact
                    .createDecision()
                    .withId("Adjudication")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            creditOfficerKS
                                                                    .getId())))
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredInput(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            supportingDocuments
                                                                    .getId())));
            Decision routingDecision = objFact
                    .createDecision()
                    .withId("Routing")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            adjudicationDecision
                                                                    .getId())));
            Decision postBureauAffordabilityDecision = objFact
                    .createDecision()
                    .withId("Post-bureau affordability")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            routingDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            affordabilityCalculation
                                                                    .getId())))
                    .withDecisionTable(getPostBureauRiskCategoryDecisionTable());
            Decision requiredMonthlyInstallmentDecision = objFact
                    .createDecision()
                    .withId("Required monthly installment")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            preBureauAffordabilityDecision
                                                                    .getId())),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            postBureauAffordabilityDecision
                                                                    .getId())))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            installmentCalculation
                                                                    .getId())));
            // TODO requested product
            Decision postBureauRiskCategoryDecision = objFact
                    .createDecision()
                    .withId("Post-bureau risk category")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            routingDecision
                                                                    .getId())),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            postBureauAffordabilityDecision
                                                                    .getId())))
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(
                                            objFact.createDmnElementReference()
                                                    .withHref(
                                                            riskManagerKS
                                                                    .getId())));
            // TODO with applicant data, bureau data

            def.withDecisions(bureauCallTypeDecision, strategyDecision,
                    eligibilityDecision, preBureauRiskCategoryDecision,
                    applicationRiskScoreDecision,
                    preBureauAffordabilityDecision,
                    postBureauAffordabilityDecision,
                    requiredMonthlyInstallmentDecision,
                    postBureauRiskCategoryDecision, routingDecision,
                    adjudicationDecision);

            TestHelper.assertSerializationProduced(def);

            dm = new DmnModel(def, null, TENANT_ID);
        }

        return dm;
    }

    protected DecisionTable getStrategyDecisionTable() throws IOException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG70_JSON_RESOURCE);

        return converter.convert(jsonModel).getDecision(CH11_FIG70_DECISION_ID)
                .getDecisionTable();
    }

    protected DecisionTable getEligibilityDecisionTable() throws IOException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG74_JSON_RESOURCE);

        return converter.convert(jsonModel).getDecision(CH11_FIG74_DECISION_ID)
                .getDecisionTable();
    }

    protected DecisionTable getPreBureauRiskCategoryDecision()
            throws IOException {
        DecisionModel jsonModel = TestHelper
                .getJsonModel(CH11_FIG76_JSON_RESOURCE);

        return converter.convert(jsonModel).getDecision(CH11_FIG76_DECISION_ID)
                .getDecisionTable();
    }
    protected DecisionTable getApplicationRiskScoreDecisionTable() {
        // build decision table from expressions
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("Application risk score model")
                .withHitPolicy(HitPolicy.COLLECT)
                .withPreferedOrientation(
                        DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i0_ie")
                                                .withText("Age"))
                                .withInputValues(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i0_iv0")
                                                .withText("[18..120]")),
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i1_ie")
                                                .withText("Marital Status"))
                                .withInputValues(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i1_iv0")
                                                .withText("S"),
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i1_iv1")
                                                .withText("M")),
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i2_ie")
                                                .withText("Employment Status"))
                                .withInputValues(
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i2_iv0")
                                                .withText("UNEMPLOYED"),
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i2_iv1")
                                                .withText("EMPLOYED"),
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i2_iv2")
                                                .withText("SELF-EMPLOYED"),
                                        objFact.createLiteralExpression()
                                                .withId("dt78_i2_iv3")
                                                .withText("STUDENT")))
                .withOutputs(
                        objFact.createDtOutput()
                                .withId("27002_dt_o1")
                                .withName("Partial score")
                                .withOutputDefinition(
                                        objFact.createDmnElementReference()
                                                .withHref("#TODO")))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("[18..21]"), emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("32")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("[22..25]"), emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("35")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("[26..35]"), emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("40")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("[36..49]"), emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("43")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText(">=50"), emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("48")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("S"), emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("25")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("M"), emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("45")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("UNEMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("15")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("STUDENT"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("18")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("EMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("45")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("SELF-EMPLOYED"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("36")));
        return dt;
    }

    protected DecisionTable getRoutingRulesDecisionTable() {
        // build decision table from expressions
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("Routing rules")
                .withHitPolicy(HitPolicy.PRIORITY)
                .withPreferedOrientation(
                        DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                        .withText("Post-Bureau Risk Category")),
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText(
                                                        "Post-Bureau Affordability")),
                        objFact.createDtInput().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Bankrupt")),
                        objFact.createDtInput()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Credit Score"))
                                .withInputValues(
                                        objFact.createLiteralExpression()
                                                .withText("null"),
                                        objFact.createLiteralExpression()
                                                .withText("[0..999]")))
                .withOutputs(
                        objFact.createDtOutput()
                                .withName("Routing")
                                .withOutputDefinition(
                                        objFact.createDmnElementReference()
                                                .withHref("#TODO")))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("false"), 
                                        emptyLE,
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("true"), 
                                        emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\""), 
                                        emptyLE, emptyLE, emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("REFER")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE,
                                        emptyLE,
                                        emptyLE,
                                        objFact.createLiteralExpression()
                                                .withText("<580"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"REFER\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyLE, emptyLE, emptyLE, emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"ACCEPT\"")));

        return dt;
    }

    protected DecisionTable getPostBureauRiskCategoryDecisionTable() {
        // build decision table from expressions
        DecisionTable dt = objFact.createDecisionTable()
                .withId("Routing rules")
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferedOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createDtInput().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Existing Customer")),
                        objFact.createDtInput().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Application Risk Score")),
                        objFact.createDtInput().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Credit Score")))
                .withOutputs(
                        objFact.createDtOutput()
                                .withName("Post Bureau Risk Category")
                                .withOutputDefinition(
                                        objFact.createDmnElementReference()
                                                .withHref("#TODO")))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("<120"),
                                        objFact.createLiteralExpression()
                                                .withText("<590"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("<120"),
                                        objFact.createLiteralExpression()
                                                .withText("[590..610]")
                                        )
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                       objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("<120"),
                                        objFact.createLiteralExpression()
                                                .withText(">610"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("LOW")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("[120..130]"),
                                        objFact.createLiteralExpression()
                                                .withText("<600"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("[120..130]"),
                                        objFact.createLiteralExpression()
                                                .withText("[600..625]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText("[120..130]"),
                                        objFact.createLiteralExpression()
                                                .withText(">625"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseLE,
                                        objFact.createLiteralExpression()
                                                .withText(">130"), emptyLE)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"VERY LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText("<=100"),
                                        objFact.createLiteralExpression()
                                                .withText("<580"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText("<=100"),
                                        objFact.createLiteralExpression()
                                                .withText("[580.600]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText("<=100"),
                                        objFact.createLiteralExpression()
                                                .withText(">600"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText(">100"),
                                        objFact.createLiteralExpression()
                                                .withText("<590"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText(">100"),
                                        objFact.createLiteralExpression()
                                                .withText("[590..615]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueLE,
                                        objFact.createLiteralExpression()
                                                .withText(">100"),
                                        objFact.createLiteralExpression()
                                                .withText(">615"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")))
                                        ;

        return dt;
    }
}
