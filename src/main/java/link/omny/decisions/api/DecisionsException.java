package link.omny.decisions.api;

public class DecisionsException extends Exception {

    public DecisionsException(String message) {
        super(message);
    }

    public DecisionsException(String msg, Exception cause) {
        super(msg, cause);
    }

}
