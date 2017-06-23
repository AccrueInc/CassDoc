package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.ServiceUnavailableException

@CompileStatic
class InterruptQueryException extends ServiceUnavailableException {
    private static final long serialVersionUID = -989546846467L

    InterruptQueryException() {
    }

    InterruptQueryException(String message) {
        super(message)
    }

    InterruptQueryException(Long retryAfter) {
        super(retryAfter)
    }

    InterruptQueryException(String message, Long retryAfter) {
        super(message, retryAfter)
    }

    InterruptQueryException(Date retryAfter) {
        super(retryAfter)
    }

    InterruptQueryException(String message, Date retryAfter) {
        super(message, retryAfter)
    }

    InterruptQueryException(Date retryAfter, Throwable cause) {
        super(retryAfter, cause)
    }

    InterruptQueryException(String message, Date retryAfter, Throwable cause) {
        super(message, retryAfter, cause)
    }

    InterruptQueryException(Long retryAfter, Throwable cause) {
        super(retryAfter, cause)
    }

    InterruptQueryException(String message, Long retryAfter, Throwable cause) {
        super(message, retryAfter, cause)
    }
}
