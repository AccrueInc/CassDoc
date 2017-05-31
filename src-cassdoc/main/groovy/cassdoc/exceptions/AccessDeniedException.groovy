package cassdoc.exceptions;

/**
 * For exceptions resulting from some access/security violation (e.g. a read-only keyspace). Note most security is handled at the REST/SpringMVC layer...
 *
 * @author a999166
 */

class AccessDeniedException extends RuntimeException {

    AccessDeniedException() {
    }

    AccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    AccessDeniedException(String message) {
        super(message);
    }

    AccessDeniedException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 328979537289573L;

}
