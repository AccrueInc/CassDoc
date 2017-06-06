package cassdoc.exceptions


/**
 * Whenever type information for provided data doesn't line up with metadata and behavioral configurations...
 *
 * @author a999166
 */

class InvalidTypeException extends RuntimeException {

    InvalidTypeException() {
    }

    InvalidTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    InvalidTypeException(String message, Throwable cause) {
        super(message, cause)
    }

    InvalidTypeException(String message) {
        super(message)
    }

    InvalidTypeException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 658979438789573L

}
