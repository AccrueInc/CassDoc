package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.BadRequestException

/**
 * For when CQL detects something already exists and that wasn't expected (create, etc).
 *
 * @author a999166
 */

@CompileStatic
class DuplicateResourceException extends BadRequestException {
    private static final long serialVersionUID = 3289794338789733L

    DuplicateResourceException() {
    }

    DuplicateResourceException(String message) {
        super(message)
    }

    DuplicateResourceException(Throwable cause) {
        super(cause)
    }

    DuplicateResourceException(String message, Throwable cause) {
        super(message, cause)
    }
}
