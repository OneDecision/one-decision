package io.onedecision.engine.decisions.api.exceptions;

import io.onedecision.engine.decisions.api.DecisionException;

public class MissingInformationRequirementException extends DecisionException {

    private static final long serialVersionUID = -762896415697242413L;

    public MissingInformationRequirementException(String msg, Exception cause) {
        super(msg, cause);
    }

    public MissingInformationRequirementException(String message) {
        super(message);
    }

}
