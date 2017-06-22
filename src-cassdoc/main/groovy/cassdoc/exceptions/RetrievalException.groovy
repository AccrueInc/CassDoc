package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.InternalServerErrorException

/**
 * error in retrieval: multiple rows when a single row expected, multiple values when a single value expected...
 *
 * @author a999166
 */

@CompileStatic
class RetrievalException extends InternalServerErrorException {
    private static final long serialVersionUID = 328595838789573L

}
