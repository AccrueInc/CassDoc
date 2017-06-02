package cassdoc.springmvc.controller

import cassdoc.API
import cassdoc.Detail
import cassdoc.OperationContext
import cwdrg.lg.annotation.Log
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
@RestController
@CompileStatic
@RequestMapping(value = '/cassdoc')
class APIController {

    @Autowired
    API api

    @RequestMapping(value = '/doc/{id}', method = RequestMethod.HEAD)
    boolean docExists(
            @PathVariable(value = 'id') String uuid,
            @RequestParam(value = 'detail', required = false) String customDetailJSON
    ) {
        OperationContext opctx = new OperationContext()
        Detail detail = new Detail()
        api.docExists(opctx, detail, uuid)
    }

    @RequestMapping(value = '/doc/{id}/{attr}', method = RequestMethod.HEAD)
    boolean docExists(
            @PathVariable(value = 'id') String uuid,
            @PathVariable(value = 'attr') String attr,
            @RequestParam(value = 'detail', required = false) String customDetailJSON
    ) {
        OperationContext opctx = new OperationContext()
        Detail detail = new Detail()
        api.attrExists(opctx, detail, uuid, attr)
    }

    @RequestMapping(value = '/doc/{id}', method = RequestMethod.GET)
    boolean getDoc(
            @PathVariable(value = 'id') String uuid,
            @RequestParam(value = 'detail', required = false) String customDetailJSON,
            HttpServletResponse response

    ) {
        OperationContext opctx = new OperationContext()
        Detail detail = new Detail()
        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)
        api.getDoc(opctx, detail, uuid, writer)
        writer.flush()
    }


    @RequestMapping(value = '/doc', method = RequestMethod.POST)
    String newDoc(
            @RequestParam(value = 'detail', required = false) String customDetailJSON,
            HttpServletRequest request
    ) {
        OperationContext opctx = new OperationContext()
        Detail detail = new Detail()

        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        api.newDoc(opctx, detail, reader)
    }

    @RequestMapping(value = '/docs', method = RequestMethod.POST)
    String newDocs(
            @RequestParam(value = 'detail', required = false) String customDetailJSON,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        OperationContext opctx = new OperationContext()
        Detail detail = new Detail()

        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)

        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)

        api.newDocList(opctx, detail, reader, writer)

        writer.flush()
    }


}
