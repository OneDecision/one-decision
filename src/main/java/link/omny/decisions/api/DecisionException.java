package link.omny.decisions.api;

public class DecisionException extends Exception {

    public DecisionException(String message) {
        super(message);
    }

    public DecisionException(String msg, Exception cause) {
        super(msg, cause);
    }

}
