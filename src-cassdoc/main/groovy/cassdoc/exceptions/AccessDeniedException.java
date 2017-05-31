package cassdoc.exceptions;

/**
 * For exceptions resulting from some access/security violation (e.g. a read-only keyspace). Note most security is handled at the REST/SpringMVC layer...
 *
 * @author a999166
 */

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 328979537289573L;

}
