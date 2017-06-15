package cassdoc.springmvc.controller

import cassdoc.CassdocAPI
import cassdoc.DocType
import cwdrg.lg.annotation.Log
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


@Log
@RestController
@CompileStatic
class AdminController {

    @Autowired
    CassdocAPI api

    @Autowired
    RequestMappingHandlerMapping handlerMappings

    @RequestMapping(value = '/admin/_collections_', method = RequestMethod.GET)
    List<String> listCollections() {
        log.dbg('GET /admin/_collections_ --> listCollections()',null)
        api.svcs.collections?.keySet() as List<String>
    }

    @RequestMapping(value = '/admin/{collection}', method = RequestMethod.GET)
    List<DocType> listDocType(
            @PathVariable(value = 'collection') String collection
    ) {
        log.dbg("GET /admin/$collection --> listDocType()",null)
        api.svcs.collections[collection].first.getTypeList()
    }

    @RequestMapping(value = '/admin/_mappings_', method = RequestMethod.GET)
    Set mappings() {
        log.dbg('GET /admin/_mappings_ --> mappings()',null)
        handlerMappings?.handlerMethods.keySet()
    }

    @RequestMapping(value = '/admin/cassdoc_system_schema', method = RequestMethod.POST)
    String createSystemSchema() {
        log.dbg('POST /admin/cassdoc_system_schema --> createSystemSchema()',null)
        if (!api.svcs.driver.keyspaces.contains('cassdoc_system_schema')) {
            api.svcs.createSystemSchema()
            return '{"system_schema_created":true}'
        }
        return '{"system_schema_created":false}'
    }

    @RequestMapping(value = '/admin/{collection}', method = RequestMethod.POST)
    String createCollection(
            @PathVariable(value = 'collection') String collection
    ) {
        log.dbg("POST /admin/$collection --> createCollection()",null)
        if (!api.svcs.driver.keyspaces.contains(collection)) {
            api.svcs.createNewCollectionSchema(collection)
        }
        api.svcs.collections = [:]
        api.svcs.loadSystemSchema()
        return """{"collection_created":"$collection"}"""
    }

    @RequestMapping(value = '/admin/{collection}/doctype', method = RequestMethod.POST)
    String createDocType(
            @PathVariable(value = 'collection') String collection,
            @RequestBody DocType docType
    ) {
        log.dbg("POST /admin/$collection/doctype (${docType.suffix} --> createDocType()",null)
        api.svcs.createNewDoctypeSchema(collection, docType)
        api.svcs.collections = [:]
        api.svcs.loadSystemSchema()
        return """{"doctype_created":"/$collection/${docType.suffix}"}"""
    }

    @RequestMapping(value = '/admin/{collection}/{typeCode}', method = RequestMethod.GET)
    DocType getDocType(
            @PathVariable(value = 'collection') String collection,
            @PathVariable(value = 'typeCode') String typeCode
    ) {
        log.dbg("GET /admin/$collection/$typeCode  --> getDocType()",null)
        api.svcs.collections[collection].first.getTypeForSuffix(typeCode)
    }

}
