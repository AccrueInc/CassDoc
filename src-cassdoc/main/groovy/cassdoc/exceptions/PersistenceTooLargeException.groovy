package cassdoc.exceptions

/**
 * General error in persistence occurred
 *
 * @author a999166
 */

class PersistenceTooLargeException extends RuntimeException {

    PersistenceTooLargeException() {
    }

    PersistenceTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    PersistenceTooLargeException(String message, Throwable cause) {
        super(message, cause)
    }

    PersistenceTooLargeException(String message) {
        super(message)
    }

    PersistenceTooLargeException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 321976438729576L

}
