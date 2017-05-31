package cassdoc.exceptions;


/**
 * General error in persistence occurred
 *
 * @author a999166
 */

class PersistenceConflictException extends RuntimeException {

    PersistenceConflictException() {
    }

    PersistenceConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    PersistenceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    PersistenceConflictException(String message) {
        super(message);
    }

    PersistenceConflictException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 321976438729576L;

}
