package link.omny.decisions.web;

import link.omny.decisions.api.DecisionsException;

public class DecisionNotFoundException extends DecisionsException {

    private static final long serialVersionUID = -7579907826337455003L;

    public DecisionNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }

    public DecisionNotFoundException(String tenantId, String definitionId, String decisionId) {
        super(String.format("Could not find %1$s.%2$s for %3$s", tenantId,
                definitionId, decisionId));
    }

}
