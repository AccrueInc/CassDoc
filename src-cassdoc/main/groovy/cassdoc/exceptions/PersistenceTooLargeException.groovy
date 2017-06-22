package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.InternalServerErrorException

/**
 * General error in persistence occurred
 *
 * @author a999166
 */

@CompileStatic
class PersistenceTooLargeException extends InternalServerErrorException {
    private static final long serialVersionUID = 321976438729576L

}
