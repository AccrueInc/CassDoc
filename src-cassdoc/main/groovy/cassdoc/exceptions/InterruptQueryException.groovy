package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.naming.ServiceUnavailableException

@CompileStatic
class InterruptQueryException extends ServiceUnavailableException {
    private static final long serialVersionUID = -989546846467L
}
