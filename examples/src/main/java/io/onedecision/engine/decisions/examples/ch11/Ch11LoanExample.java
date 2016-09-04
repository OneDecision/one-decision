package io.onedecision.engine.decisions.examples.ch11;

import io.onedecision.engine.decisions.api.DecisionConstants;
import io.onedecision.engine.decisions.model.dmn.BuiltInAggregator;
import io.onedecision.engine.decisions.model.dmn.BusinessKnowledgeModel;
import io.onedecision.engine.decisions.model.dmn.Decision;
import io.onedecision.engine.decisions.model.dmn.DecisionTable;
import io.onedecision.engine.decisions.model.dmn.DecisionTableOrientation;
import io.onedecision.engine.decisions.model.dmn.Definitions;
import io.onedecision.engine.decisions.model.dmn.DmnElement;
import io.onedecision.engine.decisions.model.dmn.DmnModel;
import io.onedecision.engine.decisions.model.dmn.HitPolicy;
import io.onedecision.engine.decisions.model.dmn.InformationItem;
import io.onedecision.engine.decisions.model.dmn.InputData;
import io.onedecision.engine.decisions.model.dmn.Invocation;
import io.onedecision.engine.decisions.model.dmn.ItemDefinition;
import io.onedecision.engine.decisions.model.dmn.KnowledgeSource;
import io.onedecision.engine.decisions.model.dmn.LiteralExpression;
import io.onedecision.engine.decisions.model.dmn.ObjectFactory;
import io.onedecision.engine.decisions.model.dmn.UnaryTests;

import java.io.IOException;

import javax.xml.namespace.QName;

/**
 * Faithfully creates the model described in chapter 11 of the specification.
 *
 * @author Tim Stephenson
 */
public class Ch11LoanExample implements DecisionConstants, ExamplesConstants {

    public static String DMN_EXAMPLES_URI = "http://www.omg.org/spec/DMN/examples/20151101/ch11example";

    public static final QName APPLICANT_DATA = new QName(DMN_EXAMPLES_URI,
            "ApplicantData", "ex");

    public static final QName BUREAU_DATA = new QName(DMN_EXAMPLES_URI,
            "BureauData", "ex");

    public static final QName REQUESTED_PRODUCT = new QName(DMN_EXAMPLES_URI,
            "RequestedProduct", "ex");

    public static final QName RISK_CATEGORY = new QName(DMN_EXAMPLES_URI,
            "RiskCategory", "ex");

    public static final QName SUPPORTING_DOCUMENTS = new QName(
            DMN_EXAMPLES_URI, "SupportingDocs", "ex");

    private static ObjectFactory objFact;

    protected UnaryTests emptyTest;
    protected UnaryTests falseTest;
    protected UnaryTests trueTest;

    private DmnModel dm;

    Ch11LoanExample() throws Exception {
        objFact = new ObjectFactory();
        emptyTest = objFact.createUnaryTests().withText("-");
        falseTest = objFact.createUnaryTests().withText("false");
        trueTest = objFact.createUnaryTests().withText("true");
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
            
            ItemDefinition bureauDataDef = objFact.createItemDefinition()
                    .withId("bureauData_t")
                    .withName("Bureau data")
                    .withItemComponent(objFact.createItemDefinition()
                            .withId("bankrupt_t")
                            .withName("Bankrupt")
                            .withTypeRef(FEEL_BOOLEAN))
                    .withItemComponent(objFact.createItemDefinition()
                            .withId("creditScore_t")
                            .withName("CreditScore")
                            .withTypeRef(FEEL_NUMBER));
            
            ItemDefinition requestedProductDef = objFact
                    .createItemDefinition()
                    .withId("requestedProduct_t")
                    .withName("Requested product")
                    .withItemComponent(
                            objFact.createItemDefinition()
                                    .withId("productType")
                                    .withName("ProductType")
                                    .withAllowedValues(
                                            objFact.createUnaryTests()
                                                    .withUnaryTests(
                                                            "STANDARD LOAN",
                                                            "SPECIAL LOAN"))
                                    .withTypeRef(FEEL_STRING))
                    .withItemComponent(
                            objFact.createItemDefinition().withId("rate")
                                    .withName("Rate").withTypeRef(FEEL_NUMBER))
                    .withItemComponent(
                            objFact.createItemDefinition().withId("term")
                                    .withName("Term").withTypeRef(FEEL_NUMBER))
                    .withItemComponent(
                            objFact.createItemDefinition().withId("amount")
                                    .withName("Amount")
                                    .withTypeRef(FEEL_NUMBER));

            ItemDefinition riskCategoryDef = objFact.createItemDefinition()
                    .withId("riskCategory_t")
                    .withName("Risk Category")
                    .withTypeRef(FEEL_STRING);
            
            ItemDefinition requiredMonthlyInstallmentDef = objFact.createItemDefinition()
.withId("monthlyInstallment_t")
                    .withName("Required Monthly Installment")
                    .withTypeRef(FEEL_NUMBER);

            ItemDefinition eligibilityDef = objFact.createItemDefinition()
                    .withId("eligigbility_t")
                    .withName("Eligibility")
                    .withAllowedValues(objFact.createUnaryTests()
                            .withUnaryTests("ELIGIBLE", "INELIGIBLE"))
                    .withTypeRef(FEEL_STRING);

            ItemDefinition bureauCallTypeDef = objFact.createItemDefinition()
                    .withId("bureauCallType_t")
                    .withName("Bureau Call Type")
                    .withAllowedValues(objFact.createUnaryTests()
                            .withUnaryTests("FULL", "MINI", "NONE"))
                    .withTypeRef(FEEL_STRING);

            ItemDefinition strategyDef = objFact.createItemDefinition()
                    .withId("strategy_t")
                    .withName("Stategy")
                    .withAllowedValues(objFact.createUnaryTests()
                            .withUnaryTests("BUREAU", "DECLINE", "THROUGH"))
                    .withTypeRef(FEEL_STRING);

            ItemDefinition routingDef = objFact
                    .createItemDefinition()
                    .withId("routing_t")
                    .withName("Routing")
                    .withAllowedValues(objFact.createUnaryTests()
                            .withUnaryTests("ACCEPT", "DECLINE", "REFER"))
                    .withTypeRef(FEEL_STRING);

            // build definitions container
            Definitions def = objFact
                    .createDefinitions()
                    .withId(CH11_DEFINITION_ID)
                    .withName("Loan Origination Decision Model")
                    .withDescription(
                            "Implements model from chapter 11 of DMN 1.1 spec")
                    .withNamespace(DMN_EXAMPLES_URI)
                    .withItemDefinitions(applicantDataDef, bureauDataDef,
                            requestedProductDef, eligibilityDef,
                            bureauCallTypeDef, strategyDef, routingDef,
                            riskCategoryDef,
                            requiredMonthlyInstallmentDef);

            // build input data
            InformationItem applicantDataItem = objFact.createInformationItem()
                    .withId("applicantId_ii")
                    .withName("Applicant data")
                    .withTypeRef(APPLICANT_DATA);
            InputData applicantDataInputData = objFact.createInputData()
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
            def.withInputData(applicantDataInputData, requestedProduct,
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

            // build business knowledge model
            BusinessKnowledgeModel bureauCallTypeBKM = objFact
                    .createBusinessKnowledgeModel()
                    .withId("bureauCallTypeTable_bkm")
                    .withName("Bureau call type table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(riskManagerKS))
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withDecisionTable(getBureauCallTypeDecisionTable())
                                    .withFormalParameter("Pre-Bureau Risk Category",FEEL_STRING))
            ;
            // TODO Context

            DecisionTable preBureauRiskCategoryDT = getPreBureauRiskCategoryDT();
            BusinessKnowledgeModel preBureauRiskCategoryTable = objFact
                    .createBusinessKnowledgeModel()
                    .withId("preBureauRiskCategoryTable_bkm")
                    .withName("Pre-bureau risk category table")
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withDecisionTable(preBureauRiskCategoryDT)
                                    .withFormalParameter(
                                            objFact.createInformationItem()
                                                    .withName("Existing Customer")
                                                    .withTypeRef(
                                                            ((ItemDefinition) applicantDataDef
                                                                    .getItemComponentByName("ExistingCustomer"))
                                                                    .getTypeRef()))
                                    .withFormalParameter("￼Application Risk Score", FEEL_NUMBER));
            // TODO Context

            BusinessKnowledgeModel applicationRiskScoreModel = objFact
                    .createBusinessKnowledgeModel()
                    .withId("applicationRiskScoreModel_bkm")
                    .withName("Application risk score model")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(analyticsKS))
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withDecisionTable(
                                            getApplicationRiskScoreModelDT())
                                    .withFormalParameter(
                                            "Age", 
                                            ((ItemDefinition) applicantDataDef
                                                    .getItemComponentByName("Age"))
                                                    .getTypeRef())
                                    .withFormalParameter(
                                            "MaritalStatus",
                                            ((ItemDefinition) applicantDataDef
                                                    .getItemComponentByName("MaritalStatus"))
                                                    .getTypeRef())
                                    .withFormalParameter(
                                            "Employment Status",
                                            ((ItemDefinition) applicantDataDef
                                                    .getItemComponentByName("EmploymentStatus"))
                                                    .getTypeRef()));
            // TODO Context

            DecisionTable creditContingencyFactorDecisionTable = getCreditContingencyFactorDT();
            BusinessKnowledgeModel creditContingencyFactorTable = objFact
                    .createBusinessKnowledgeModel()
                    .withId("creditContingencyFactorTable_bkm")
                    .withName("Credit contingency factor table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(riskManagerKS))
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withFormalParameter("Risk Category", RISK_CATEGORY)
                                    .withDecisionTable(
                                            creditContingencyFactorDecisionTable));
            InformationItem riskCategory = objFact.createInformationItem()
                    .withId("riskCategory_ii")
                    .withName("Risk Category")
                    .withTypeRef(riskCategoryDef.getTypeRef());
            BusinessKnowledgeModel affordabilityCalculation = getAffordabilityCalculation(
                    affordabilityKS, creditContingencyFactorDecisionTable,
                    creditContingencyFactorTable, riskCategory)
            ;
            // TODO the information items here should probably not be created
            // here but reference pre-existing
            BusinessKnowledgeModel eligibilityRulesBKM = objFact
                    .createBusinessKnowledgeModel()
                    .withId("eligibilityRules_bkm")
                    .withName("Eligibility rules")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(productSpecKS))
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withDecisionTable(getEligibilityRulesDT())
                                    .withFormalParameter("Pre-Bureau Affordability", FEEL_NUMBER)
                                    .withFormalParameter("Pre-Bureau Risk Category", FEEL_STRING)
                                    .withFormalParameter("Age", FEEL_NUMBER));
            
            BusinessKnowledgeModel routingRulesBKM = objFact.createBusinessKnowledgeModel()
                    .withId("routingRules_bkm")
                    .withName("Routing rules")
                    .withEncapsulatedLogic(objFact.createFunctionDefinition()
                            .withDecisionTable(getRoutingRulesDT())
                            .withFormalParameter("Bankrupt", FEEL_BOOLEAN)
                            .withFormalParameter("Credit Score", FEEL_NUMBER)
                            .withFormalParameter("Post-Bureau Risk Category", FEEL_NUMBER)
                            .withFormalParameter("Post-Bureau Affordability", FEEL_NUMBER));
            // TODO Context

            BusinessKnowledgeModel installmentCalculationBKM = getInstallmentCalculation();

            DecisionTable postBureauRiskCategoryDT = getPostBureauRiskCategoryDT();
            BusinessKnowledgeModel postBureauRiskCategoryBKM = objFact
                    .createBusinessKnowledgeModel()
                    .withId("postBureauRiskCategoryTable_bkm")
                    .withName("Post-bureau risk category table")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(riskManagerKS))
                    .withEncapsulatedLogic(
                            objFact.createFunctionDefinition()
                                    .withDecisionTable(
                                            postBureauRiskCategoryDT)
                                    .withFormalParameter(
                                            "Existing Customer",
                                            ((ItemDefinition) applicantDataDef
                                                    .getItemComponentByName("ExistingCustomer"))
                                                    .getTypeRef())
                                    .withFormalParameter("Application Risk Score",DecisionConstants.FEEL_NUMBER)
                                    .withFormalParameter("Credit Score",FEEL_NUMBER));
                    // TODO Context??
            
            def.withBusinessKnowledgeModels(bureauCallTypeBKM,
                    preBureauRiskCategoryTable, applicationRiskScoreModel,
                    creditContingencyFactorTable, affordabilityCalculation,
                    eligibilityRulesBKM, routingRulesBKM, installmentCalculationBKM,
                    postBureauRiskCategoryBKM);

            // build decisions
            Decision applicationRiskScoreD = objFact
                    .createDecision()
                    .withId("applicationRiskScore_d")
                    .withName("Application risk score")
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            applicationRiskScoreModel))
                    .withInvocation(
                            objFact.createInvocation()
                                    .withCalledFunction(
                                            applicationRiskScoreModel.getName())
                            .withBinding(
                                     objFact.createBinding()
                                             .withParameter("Age")
                                             .withLiteralExpression("Applicant data . Age"),
                                     objFact.createBinding()
                                             .withParameter("Marital Status")
                                             .withLiteralExpression("Applicant data . MaritalStatus"),
                                     objFact.createBinding()
                                             .withParameter("Employment Status")
                                             .withLiteralExpression("Applicant data . EmploymentStatus")))
                  ;


            Decision preBureauRiskCategoryD = objFact.createDecision()
                    .withId("preBureauRiskCategory_d")
                    .withName("Pre-bureau risk category") // TODO Fig 66 has Risk
                    .withInformationRequirements(applicationRiskScoreD,
                            applicantDataInputData)
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            preBureauRiskCategoryTable))
                    .withInvocation(
                            objFact.createInvocation()
                                    .withCalledFunction(preBureauRiskCategoryTable.getName())
                            .withBinding(
                                     objFact.createBinding()
                                             .withParameter("Existing Customer")
                                             .withLiteralExpression("Applicant data . ExistingCustomer"),
                                     objFact.createBinding()
                                             .withParameter("Application Risk Score")
                                             .withLiteralExpression("Application risk score"))
                    );
            ;
            Decision requiredMonthlyInstallmentD = getRequiredMonthylInstallmentDecision(
                    requestedProduct, requestedProductDef,
                    installmentCalculationBKM);

            Decision preBureauAffordabilityD = objFact
                    .createDecision()
                    .withId("preBureauAffordability_d")
                    .withName("Pre-bureau affordability")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredInput(applicantDataInputData),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            preBureauRiskCategoryD),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            requiredMonthlyInstallmentD))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            affordabilityCalculation))
                    .withInvocation(
                            getAffordabilityCalculationInvocation(
                                    affordabilityCalculation, applicantDataDef,
                                    riskCategoryDef,
                                    requiredMonthlyInstallmentDef, true));

            Decision eligibilityD = objFact.createDecision()
                    .withId("eligibility_d")
                    .withName("Eligibility")
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            preBureauRiskCategoryD),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            preBureauAffordabilityD),
                            objFact.createInformationRequirement()
                                    .withRequiredInput(applicantDataInputData))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(eligibilityRulesBKM))
                    .withInvocation(
                            objFact.createInvocation()
                                    .withCalledFunction(eligibilityRulesBKM.getName())
                                    .withBinding(
                                             objFact.createBinding()
                                                    .withParameter("Age")
                                                    .withLiteralExpression("Applicant data.Age"),
                                             objFact.createBinding()
                                                     .withParameter("Pre-Bureau Risk Category")
                                                     .withLiteralExpression("Pre-bureau risk category"),
                                             objFact.createBinding()
                                                     .withParameter("Pre-Bureau Affordability")
                                                     .withLiteralExpression("Pre-bureau affordability"))
                            );

            InformationItem bureauCallType = objFact.createInformationItem()
                    .withId("bureauCallType_ii")
                    .withName("Bureau Call Type")
                    .withTypeRef(FEEL_STRING);
            Decision bureauCallTypeD = objFact.createDecision()
                    .withId("bureauCallType_d")
                    .withName("Bureau call type")
                    .withVariable(bureauCallType)
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(
                                            preBureauRiskCategoryTable))
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(bureauCallTypeBKM))
                    .withInvocation(
                            objFact.createInvocation()
                                    .withCalledFunction(bureauCallTypeBKM.getName())
                                    .withBinding(
                                            objFact.createBinding()
                                                    .withParameter("Pre-Bureau Risk Category")
                                                    .withLiteralExpression("Pre-Bureau Risk Category")))
                    ;
            Decision strategyD = getStrategyDecision()
                    .withInformationRequirements(
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(eligibilityD),
                            objFact.createInformationRequirement()
                                    .withRequiredDecision(bureauCallTypeD));
            
            Decision adjudicationD = objFact.createDecision()
                    .withId("adjudication_d")
                    .withName("Adjudication")
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(creditOfficerKS))
                    .withInformationRequirements(applicantDataInputData,
                            bureauData, supportingDocuments);

            Decision postBureauAffordabilityD = objFact
                    .createDecision()
                    .withId("postBureauAffordability_d")
                    .withName("Post-bureau affordability")
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            affordabilityCalculation))
                    .withInvocation(
                            getAffordabilityCalculationInvocation(
                                    affordabilityCalculation, applicantDataDef,
                                    riskCategoryDef,
                                    requiredMonthlyInstallmentDef, false));

            Decision postBureauRiskCategoryDecision = objFact
                    .createDecision()
                    .withId("postBureauRiskCategory_d")
                    .withName("Post-bureau risk category")
                    .withInformationRequirements(applicantDataInputData,
                            bureauData)
                    .withKnowledgeRequirements(
                            objFact.createKnowledgeRequirement()
                                    .withRequiredKnowledge(
                                            postBureauRiskCategoryBKM))
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(riskManagerKS))
                    .withInvocation(objFact.createInvocation()
                                    .withCalledFunction(postBureauRiskCategoryBKM.getName())
                            .withBinding(
                                    objFact.createBinding()
                                            .withParameter("Existing Customer")
                                            .withLiteralExpression("Applicant data . ExistingCustomer"),
                                    objFact.createBinding()
                                            .withParameter("Credit Score")
                                            .withLiteralExpression("Bureau data . CreditScore"),
                                    objFact.createBinding()
                                            .withParameter("￼Application Risk Score")
                                            .withLiteralExpression("Application risk score"))                                        
                            );

            Decision routingD = objFact
                    .createDecision()
                    .withId("routing_d")
                    .withName("Routing")
                    .withInformationRequirements(
                            postBureauAffordabilityD,
                            postBureauRiskCategoryDecision, bureauData)
                    .withAuthorityRequirements(
                            objFact.createAuthorityRequirement()
                                    .withRequiredAuthority(routingRulesBKM))
                    .withInvocation(objFact.createInvocation()
                                    .withCalledFunction(routingRulesBKM.getName())
                            .withBinding(
                                    objFact.createBinding()
                                            .withParameter("Bankrupt")
                                            .withLiteralExpression("Bureau data . Bankrupt"),
                                    objFact.createBinding()         
                                            .withParameter("Credit Score")
                                            .withLiteralExpression("Bureau data . CreditScore"),
                                    objFact.createBinding()        
                                            .withParameter("￼Post-Bureau Risk Category")
                                            .withLiteralExpression("Post-bureau risk category"),
                                    objFact.createBinding()                                 
                                            .withParameter("Post-Bureau Affordability")
                                            .withLiteralExpression("Post-bureau affordability"))
                                   );

            def.withDecisions(bureauCallTypeD, strategyD,
                    eligibilityD, preBureauRiskCategoryD,
                    applicationRiskScoreD,
                    preBureauAffordabilityD,
                    postBureauAffordabilityD,
                    requiredMonthlyInstallmentD,
                    postBureauRiskCategoryDecision, routingD,
                    adjudicationD);

            dm = new DmnModel(def, null, TENANT_ID);
        }

        return dm;
    }

    private BusinessKnowledgeModel getAffordabilityCalculation(
            KnowledgeSource affordabilityKS,
            DecisionTable creditContingencyFactorDecisionTable,
            BusinessKnowledgeModel creditContingencyFactorTable,
            InformationItem riskCategory) {
        return objFact
                .createBusinessKnowledgeModel()
                .withId("AffordabilityCalculation_bkm")
                .withName("Affordability calculation")
                .withKnowledgeRequirements(
                        objFact.createKnowledgeRequirement()
                                .withRequiredKnowledge(
                                        creditContingencyFactorTable))
                .withAuthorityRequirements(
                        objFact.createAuthorityRequirement()
                                .withRequiredAuthority(affordabilityKS))
                .withEncapsulatedLogic(
                        objFact.createFunctionDefinition()
                                .withFormalParameter("Monthly Income", FEEL_NUMBER)
                                .withFormalParameter("Monthly Repayments", FEEL_NUMBER)
                                .withFormalParameter("Monthly Expenses", FEEL_NUMBER)
                                .withFormalParameter(riskCategory)
                                .withFormalParameter(
                                        "Required Monthly Installment",
                                        FEEL_NUMBER)
                                .withContext(
                                        objFact.createContext()
                                                .withContextEntry(
                                                        objFact.createContextEntry()
                                                                .withVariable(
                                                                        "Disposible Income")
                                                                .withLiteralExpression(
                                                                        "Monthly Income – (Monthly Repayments + Monthly Expenses)"),
                                                        objFact.createContextEntry()
                                                                .withVariable(
                                                                        "Credit Contingency Factor")
                                                                .withInvocation(
                                                                        objFact.createInvocation()
                                                                                .withCalledFunction("Credit contingency factor table")
                                                                                .withBinding(
                                                                                        objFact.createBinding()
                                                                                                .withParameter(
                                                                                                        "Risk Category")
                                                                                                .withLiteralExpression("Risk Category"))),
                                                        objFact.createContextEntry()
                                                                .withVariable(
                                                                        "Affordability")
                                                                .withLiteralExpression(
                                                                        "if Disposable Income * Credit Contingency Factor > Required Monthly Installment then true\nelse false"),
                                                        objFact.createContextEntry()
                                                                .withLiteralExpression(
                                                                        "Affordability"))));
    }


    private Invocation getAffordabilityCalculationInvocation(
            BusinessKnowledgeModel affordabilityCalculation,
            ItemDefinition applicantDataDef, DmnElement riskCategoryDef,
            DmnElement requiredMonthlyInstallmentDef, boolean isPreBureau) {

        LiteralExpression riskCategoryLE = objFact.createLiteralExpression();
        if (isPreBureau) {
            riskCategoryLE.withText("Pre-bureau risk category");
        } else {
            riskCategoryLE.withText("Post-bureau risk category");
        }
        return objFact
                .createInvocation()
                .withCalledFunction(affordabilityCalculation.getName())
                .withBinding(
                        objFact.createBinding()
                                .withParameter("￼Monthly Income")
                                .withLiteralExpression("Applicant data . Monthly . Income"),
                        objFact.createBinding()
                                .withParameter("￼Monthly Repayments")
                                .withLiteralExpression("Applicant data . Monthly . Repayments"),
                        objFact.createBinding()
                                .withParameter("Monthly Expenses")
                                .withLiteralExpression("Applicant data . Monthly . Expenses"),
                        objFact.createBinding()
                                .withParameter("￼Risk Category")
                                .withLiteralExpression(riskCategoryLE),
                        objFact.createBinding()
                                .withParameter("￼Required Monthly Installment")
                                .withLiteralExpression("Required monthly installment")
                );
    }

    private DecisionTable getCreditContingencyFactorDT() {
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("creditContingencyFactor_dt")
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Risk Category"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("DECLINE",
                                                        "HIGH", "MEDIUM",
                                                        "LOW", "VERY LOW")))
                .withOutputs(
                        objFact.createOutputClause()
                                .withName("Credit Contingency Factor"))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "\"HIGH\",\"DECLINE\""))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("0.6")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "MEDIUM"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("0.7")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "\"LOW\",\"VERY LOW\""))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("0.8")));
        return dt;
    }

    private Decision getRequiredMonthylInstallmentDecision(
            InputData requestedProduct, ItemDefinition requestedProductDef,
            BusinessKnowledgeModel installmentCalculation) {
        return objFact
                .createDecision()
                .withId("RequiredMonthlyInstallment_d")
                .withName("Required monthly installment")
                .withInformationRequirements(
                        objFact.createInformationRequirement()
                                .withRequiredInput(requestedProduct))
                .withKnowledgeRequirements(
                        objFact.createKnowledgeRequirement()
                                .withRequiredKnowledge(installmentCalculation))
                .withInvocation(
                        objFact.createInvocation()
                                .withCalledFunction("Installment calculation")
                                .withBinding(
                                        objFact.createBinding()
                                                .withParameter("Product Type")
                                                .withLiteralExpression("Requested product . ProductType"),
                                        objFact.createBinding()
                                                .withParameter("Rate")
                                                .withLiteralExpression("Requested product . Rate"),
                                        objFact.createBinding()
                                                .withParameter("Term")
                                                .withLiteralExpression("Requested product . Term"),
                                        objFact.createBinding()
                                                .withParameter("Amount")
                                                .withLiteralExpression("Requested product . Amount")                                                                      
                        ));
    }

    private BusinessKnowledgeModel getInstallmentCalculation() {
        return objFact
                .createBusinessKnowledgeModel()
                .withId("installmentCalculation_bkm")
                .withName("Installment calculation")
                .withEncapsulatedLogic(
                        objFact.createFunctionDefinition()
                                .withFormalParameter("Product Type",FEEL_STRING)
                                .withFormalParameter("Rate",FEEL_NUMBER)
                                .withFormalParameter("Term",FEEL_NUMBER)
                                .withFormalParameter( "Amount", FEEL_NUMBER)
                                .withContext(
                                        objFact.createContext()
                                                .withContextEntry(
                                                        objFact.createContextEntry()
                                                                .withVariable("Monthly Fee")
                                                                .withLiteralExpression("if Product Type = \"STANDARD LOAN\" then 20.00\nelse if Product Type = \"SPECIAL LOAN\" then 25.00\n else null"),
                                                        objFact.createContextEntry()
                                                                .withVariable("Monthly Repayment")
                                                                .withLiteralExpression("PMT(Rate, Term, Amount)"),
                                                        objFact.createContextEntry()
                                                                .withLiteralExpression("Monthly Repayment + Monthly Fee"))

                                ));
    }

    protected Decision getStrategyDecision() throws IOException {
        // TODO reimplement with fluent API
        throw new IllegalStateException("Not yet implemented");
    }

    protected DecisionTable getBureauCallTypeDecisionTable() throws IOException {
        // TODO reimplement with fluent API
        throw new IllegalStateException("Not yet implemented");
    }

    protected DecisionTable getEligibilityRulesDT()
            throws IOException {
        // TODO reimplement with fluent API
        throw new IllegalStateException("Not yet implemented");
    }

    protected DecisionTable getPreBureauRiskCategoryDT()
            throws IOException {
        // TODO reimplement with fluent API
        throw new IllegalStateException("Not yet implemented");
    }

    protected DecisionTable getApplicationRiskScoreModelDT() {
        // build decision table from expressions
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("applicationRiskScoreModel_dt")
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

    protected DecisionTable getRoutingRulesDT() {
        // build decision table from expressions
        DecisionTable dt = objFact
                .createDecisionTable()
                .withId("routingRules_dt")
                .withHitPolicy(HitPolicy.PRIORITY)
                .withPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Post-Bureau Risk Category")),
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Post-Bureau Affordability")),
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Bankrupt")),
                        objFact.createInputClause()
                                .withInputExpression(
                                        objFact.createLiteralExpression()
                                                .withText("Credit Score"))
                                .withInputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("null",
                                                        "[0..999]")))
                .withOutputs(
                        objFact.createOutputClause()
                                .withName("Routing")
                                .withOutputValues(
                                        objFact.createUnaryTests()
                                                .withUnaryTests("DECLINE",
                                                        "REFER", "ACCEPT")))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        objFact.createUnaryTests().withText(
                                                "false"), emptyTest, emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests().withText(
                                                "true"), emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("DECLINE")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        objFact.createUnaryTests().withText(
                                                "\"HIGH\""), emptyTest,
                                        emptyTest, emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("REFER")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        emptyTest,
                                        emptyTest,
                                        emptyTest,
                                        objFact.createUnaryTests().withText(
                                                "<580"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"REFER\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(emptyTest, emptyTest,
                                        emptyTest, emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"ACCEPT\"")));

        return dt;
    }

    protected DecisionTable getPostBureauRiskCategoryDT() {

        // build decision table from expressions
        DecisionTable dt = objFact.createDecisionTable()
                .withId("postBureauRiskCategory_dt")
                .withHitPolicy(HitPolicy.UNIQUE)
                .withPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW)
                .withInputs(
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Existing Customer")),
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Application Risk Score")),
                        objFact.createInputClause().withInputExpression(
                                objFact.createLiteralExpression().withText(
                                        "Credit Score")))
                .withOutputs(
                        objFact.createOutputClause()
                                .withName("Post Bureau Risk Category"))
                .withRules(
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("<120"),
                                        objFact.createUnaryTests()
                                                .withText("<590"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("<120"),
                                        objFact.createUnaryTests()
                                                .withText("[590..610]")
                                        )
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                       objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("<120"),
                                        objFact.createUnaryTests()
                                                .withText(">610"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("LOW")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("[120..130]"),
                                        objFact.createUnaryTests()
                                                .withText("<600"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("[120..130]"),
                                        objFact.createUnaryTests()
                                                .withText("[600..625]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests()
                                                .withText("[120..130]"),
                                        objFact.createUnaryTests()
                                                .withText(">625"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        falseTest,
                                        objFact.createUnaryTests().withText(
                                                ">130"), emptyTest)
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"VERY LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText("<=100"),
                                        objFact.createUnaryTests()
                                                .withText("<580"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText("<=100"),
                                        objFact.createUnaryTests()
                                                .withText("[580.600]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText("<=100"),
                                        objFact.createUnaryTests()
                                                .withText(">600"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText(">100"),
                                        objFact.createUnaryTests()
                                                .withText("<590"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"HIGH\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText(">100"),
                                        objFact.createUnaryTests()
                                                .withText("[590..615]"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"MEDIUM\"")),
                        objFact.createDecisionRule()
                                .withInputEntry(
                                        trueTest,
                                        objFact.createUnaryTests()
                                                .withText(">100"),
                                        objFact.createUnaryTests()
                                                .withText(">615"))
                                .withOutputEntry(
                                        objFact.createLiteralExpression()
                                                .withText("\"LOW\"")))
                                        ;

        return dt;
    }
}
