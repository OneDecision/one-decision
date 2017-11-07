package io.onedecision.engine.decisions.api.exceptions;

import io.onedecision.engine.decisions.api.DecisionException;

public class DecisionIllegalArgumentException extends DecisionException {

    private static final long serialVersionUID = 6763044620488182749L;

    public DecisionIllegalArgumentException(String msg, Exception cause) {
        super(msg, cause);
    }

    public DecisionIllegalArgumentException(String msg) {
        super(msg);
    }
}
