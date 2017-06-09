package cassdoc.springmvc.controller

import cassdoc.CassdocAPI
import cassdoc.Detail
import cassdoc.springmvc.service.CtxDtl
import cassdoc.springmvc.service.PrepareCtx
import cwdrg.lg.annotation.Log
import cwdrg.spring.annotation.RequestParamJSON
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Log
@RestController
@CompileStatic
@RequestMapping(value = '/doc')
// TODO: '/meta'
class APIController {
    @Autowired
    PrepareCtx prep

    @Autowired
    CassdocAPI api

    @RequestMapping(method = RequestMethod.GET)
    String status() {
        return '{"webappStatus":"up"}'
    }

    @RequestMapping(value = '/{collection}/{id}', method = RequestMethod.HEAD)
    boolean docExists(
            @PathVariable(value = 'collection') String collection,
            @PathVariable(value = 'id') String uuid,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        api.docExists(ctxDtl.ctx, ctxDtl.dtl, uuid)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.HEAD)
    boolean attrExists(
            @PathVariable(value = 'collection') String collection,
            @PathVariable(value = 'id') String uuid,
            @PathVariable(value = 'attr') String attr,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        api.attrExists(ctxDtl.ctx, ctxDtl.dtl, uuid, attr)
    }

    @RequestMapping(value = '/{collection}/{id}', method = RequestMethod.GET)
    boolean getDoc(
            @PathVariable(value = 'collection') String collection,
            @PathVariable(value = 'id') String uuid,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletResponse response

    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)
        api.getDoc(ctxDtl.ctx, ctxDtl.dtl, uuid, writer)
        writer.flush()
    }

    @RequestMapping(value = '/{collection}/', method = RequestMethod.POST)
    String newDoc(
            @PathVariable(value = 'collection') String collection,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        api.newDoc(ctxDtl.ctx, ctxDtl.dtl, reader)
    }

    @RequestMapping(value = '/{collection}/list', method = RequestMethod.POST)
    String newDocs(
            @PathVariable(value = 'collection') String collection,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)

        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)

        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)

        api.newDocList(ctxDtl.ctx, ctxDtl.dtl, reader, writer)

        writer.flush()
    }

}
