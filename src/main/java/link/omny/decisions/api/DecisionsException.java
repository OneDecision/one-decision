package link.omny.decisions.api;

public class DecisionsException extends RuntimeException {

	private static final long serialVersionUID = 434480901378400857L;

	public DecisionsException(String message) {
        super(message);
    }

    public DecisionsException(String msg, Exception cause) {
        super(msg, cause);
    }

}
