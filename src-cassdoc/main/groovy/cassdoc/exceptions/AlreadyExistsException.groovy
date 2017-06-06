package cassdoc.exceptions


/**
 * For when CQL detects something already exists and that wasn't expected (create, etc).
 *
 * @author a999166
 */

class AlreadyExistsException extends RuntimeException {

    AlreadyExistsException() {
    }

    AlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    AlreadyExistsException(String message, Throwable cause) {
        super(message, cause)
    }

    AlreadyExistsException(String message) {
        super(message)
    }

    AlreadyExistsException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 3289794338789733L

}
