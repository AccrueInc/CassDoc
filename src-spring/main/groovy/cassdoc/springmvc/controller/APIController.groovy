package cassdoc.springmvc.controller

import cassdoc.CassdocAPI
import cassdoc.Detail
import cassdoc.springmvc.service.CtxDtl
import cassdoc.springmvc.service.PrepareCtx
import cwdrg.lg.annotation.Log
import cwdrg.spring.annotation.RequestParamJSON
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
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
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        api.docExists(ctxDtl.ctx, ctxDtl.dtl, uuid)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.HEAD)
    boolean attrExists(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        api.attrExists(ctxDtl.ctx, ctxDtl.dtl, uuid, attr)
    }

    @RequestMapping(value = '/{collection}/{id}', method = RequestMethod.GET)
    void getDoc(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @RequestParam(value = 'simple', required = false) Boolean simple = false,
            @RequestParam(value = 'jsonPath', required = false) String jsonPath = null,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletResponse response

    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)
        if (simple) {
            api.getSimpleDoc(ctxDtl.ctx, ctxDtl.dtl, uuid, writer)
        } else if (jsonPath != null) {
            api.getDocJsonPath(ctxDtl.ctx,ctxDtl.dtl,uuid,jsonPath, writer)
        } else {
            api.getDoc(ctxDtl.ctx, ctxDtl.dtl, uuid, writer)
        }
        writer.flush()
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.GET)
    void getAttr(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParam(value = 'simple', required = false) Boolean simple,
            @RequestParam(value = 'jsonPath', required = false) String jsonPath = null,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletResponse response

    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)
        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)
        if (simple) {
            api.getSimpleAttr(ctxDtl.ctx, ctxDtl.dtl, uuid, attr, writer)
        } else if (jsonPath != null) {
            api.getAttrJsonPath(ctxDtl.ctx,ctxDtl.dtl,uuid,attr, jsonPath, writer)
        } else {
            api.getAttr(ctxDtl.ctx, ctxDtl.dtl, uuid, attr, writer)
        }
        writer.flush()
    }

    @RequestMapping(value = '/{collection}', method = RequestMethod.PUT)
    String newDoc(
            @PathVariable(value = 'collection', required = true) String collection,
            @RequestParam(value = 'async', required = false) Boolean async = false,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        // TODO: figure out async use cases
        api.newDoc(ctxDtl.ctx, ctxDtl.dtl, reader)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.PUT)
    String newAttr(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParam(value = 'async', required = false) Boolean async = false,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        // TODO: figure out async use cases
        api.newAttr(ctxDtl.ctx, ctxDtl.dtl, uuid, attr, reader, false)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.POST)
    void updateAttr(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParam(value = 'async', required = false) Boolean async = false,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        String json = IOUtils.toString(reader)
        // TODO: figure out async use cases
        api.updateAttr(ctxDtl.ctx, ctxDtl.dtl, uuid, attr, json)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.PATCH)
    void overlayAttr(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParam(value = 'async', required = false) Boolean async = false,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)
        String json = IOUtils.toString(reader)
        // TODO: figure out async use cases
        api.updateAttrOverlay(ctxDtl.ctx, ctxDtl.dtl, uuid, attr, json)
    }

    @RequestMapping(value = '/{collection}/list', method = RequestMethod.PUT)
    String newDocs(
            @PathVariable(value = 'collection', required = true) String collection,
            @RequestParam(value = 'async', required = false) Boolean async = false,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CtxDtl ctxDtl = prep.ctxAndDtl(collection, customDetailJSON)

        ServletInputStream instream = request.inputStream
        Reader reader = new InputStreamReader(instream)

        ServletOutputStream outstream = response.outputStream
        Writer writer = new OutputStreamWriter(outstream)

        // TODO: figure out async use cases
        api.newDocList(ctxDtl.ctx, ctxDtl.dtl, reader, writer)

        writer.flush()
    }

    @RequestMapping(value = '/{collection}/{id}', method = RequestMethod.DELETE)
    void delDoc(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        api.delDoc(ctxDtl.ctx, ctxDtl.dtl, uuid)
    }

    @RequestMapping(value = '/{collection}/{id}/{attr}', method = RequestMethod.DELETE)
    void delAttr(
            @PathVariable(value = 'collection', required = true) String collection,
            @PathVariable(value = 'id', required = true) String uuid,
            @PathVariable(value = 'attr', required = true) String attr,
            @RequestParamJSON(value = 'detail', required = false) Detail customDetailJSON
    ) {
        CtxDtl ctxDtl= prep.docExists(collection, customDetailJSON)
        api.delAttr(ctxDtl.ctx, ctxDtl.dtl, uuid, attr)
    }
    
}
