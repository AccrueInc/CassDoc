package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.InternalServerErrorException

@CompileStatic
class UnexpectedPersistenceStateException extends InternalServerErrorException {
    private static final long serialVersionUID = 3289774963321789573L

    UnexpectedPersistenceStateException() {
    }

    UnexpectedPersistenceStateException(String message) {
        super(message)
    }

    UnexpectedPersistenceStateException(Throwable cause) {
        super(cause)
    }

    UnexpectedPersistenceStateException(String message, Throwable cause) {
        super(message, cause)
    }
}
