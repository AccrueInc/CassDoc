package cassdoc.exceptions;


/**
 * Whenever type information for provided data doesn't line up with metadata and behavioral configurations...
 *
 * @author a999166
 */

public class InvalidTypeException extends RuntimeException {

    public InvalidTypeException() {
    }

    public InvalidTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTypeException(String message) {
        super(message);
    }

    public InvalidTypeException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 658979438789573L;

}
