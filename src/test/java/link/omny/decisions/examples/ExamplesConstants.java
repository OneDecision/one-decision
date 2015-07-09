package link.omny.decisions.examples;

public interface ExamplesConstants {

	static final String TENANT_ID = "examples";

    // Application Risk Rating example
    static final String ARR_JSON_RESOURCE = "/decisions/examples/ApplicationRiskRating.json";
    static final String ARR_DMN_RESOURCE = "/decisions/examples/ApplicationRiskRating.dmn";
    static final String ARR_DEFINITION_ID = "ApplicationRiskRating";
    static final String ARR_DECISION_ID = "DetermineApplicantRiskRating";

    // Email Follow Up example
    static final String EFU_JSON_RESOURCE = "/decisions/examples/EmailFollowUp.json";
    static final String EFU_DMN_RESOURCE = "/decisions/examples/EmailFollowUp.dmn";
    static final String EFU_DEFINITION_ID = "EmailFollowUp";
    static final String EFU_DECISION_ID = "DetermineEmailToSend";
}
