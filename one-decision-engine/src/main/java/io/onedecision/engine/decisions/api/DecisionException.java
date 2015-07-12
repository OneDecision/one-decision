package io.onedecision.engine.decisions.api;

public class DecisionException extends RuntimeException {

	private static final long serialVersionUID = 434480901378400857L;

	public DecisionException(String message) {
        super(message);
    }

    public DecisionException(String msg, Exception cause) {
        super(msg, cause);
    }

}
