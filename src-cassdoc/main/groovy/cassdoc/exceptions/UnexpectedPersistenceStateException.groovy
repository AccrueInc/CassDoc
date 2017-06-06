package cassdoc.exceptions

import groovy.transform.CompileStatic

@CompileStatic
class UnexpectedPersistenceStateException extends RuntimeException {

    /**
     * Exception raised when something expected (relation mapping/backrefs, indexes, etc) is not found, and it may indicate a corrupted object graph for an
     * Entity or Attribute or Value
     */

    UnexpectedPersistenceStateException() {
    }


    UnexpectedPersistenceStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace)
    }


    UnexpectedPersistenceStateException(String message, Throwable cause) {
        super(message, cause)
    }


    UnexpectedPersistenceStateException(String message) {
        super(message)
    }


    UnexpectedPersistenceStateException(Throwable cause) {
        super(cause)
    }

    private static final long serialVersionUID = 3289774963321789573L
}
