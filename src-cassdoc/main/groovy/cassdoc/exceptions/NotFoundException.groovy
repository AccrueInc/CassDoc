package cassdoc.exceptions


/**
 * For when an expected value is not found in the database (dangling ids/relations/links/etc)
 *
 * @author a999166
 */

class NotFoundException extends RuntimeException {

    NotFoundException() {
    }

    NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }

    NotFoundException(String message, Throwable cause) {
        super(message, cause)
    }

    NotFoundException(String message) {
        super(message)
    }

    NotFoundException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 638979438789573L

}
