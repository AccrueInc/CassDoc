package cassdoc.exceptions


/**
 * error in retrieval: multiple rows when a single row expected, multiple values when a single value expected...
 *
 * @author a999166
 */

class RetrievalException extends RuntimeException {

    RetrievalException() {
    }

    RetrievalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    RetrievalException(String message, Throwable cause) {
        super(message, cause)
    }

    RetrievalException(String message) {
        super(message)
    }

    RetrievalException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 328595838789573L

}
