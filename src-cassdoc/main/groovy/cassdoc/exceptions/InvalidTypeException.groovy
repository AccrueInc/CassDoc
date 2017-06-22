package cassdoc.exceptions

import groovy.transform.CompileStatic

import javax.ws.rs.BadRequestException

/**
 * Whenever type information for provided data doesn't line up with metadata and behavioral configurations...
 *
 * @author a999166
 */

@CompileStatic
class InvalidTypeException extends BadRequestException {
    private static final long serialVersionUID = 658979438789573L

}
