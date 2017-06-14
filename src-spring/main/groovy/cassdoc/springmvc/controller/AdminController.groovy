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


@Log
@RestController
@CompileStatic
@RequestMapping(value = '/admin')
class AdminController {

    @Autowired
    CassdocAPI api

    @RequestMapping(value = 'cassdoc_system_schema', method = RequestMethod.POST)
    String createSystemSchema()
    {
        if (!api.svcs.driver.keyspaces.contains('cassdoc_system_schema')) {
            api.svcs.createSystemSchema()
            return '{"system_schema_created":true}'
        }
        return '{"system_schema_created":false}'
    }

    @RequestMapping(value = '{collection}', method = RequestMethod.POST)
    String createCollection(
            @PathVariable(value = 'collection') String collection
    ) {
        if (!api.svcs.driver.keyspaces.contains(collection)) {
            api.svcs.createNewCollectionSchema(collection)
        }
        api.svcs.collections = [:]
        api.svcs.loadSystemSchema()
        return """{"collection_created":"$collection"}"""
    }

    @RequestMapping(value = '{collection}/doctype', method = RequestMethod.POST)
    String createDocType(
            @PathVariable(value = 'collection') String collection,
            @RequestBody DocType docType
    ) {
        api.svcs.createNewDoctypeSchema(collection, docType)
        api.svcs.collections = [:]
        api.svcs.loadSystemSchema()
        return """{"doctype_created":"/$collection/${docType.suffix}"}"""
    }

    @RequestMapping(value = '{collection}/{typeCode}', method = RequestMethod.GET)
    DocType getDocType(
            @PathVariable(value = 'collection') String collection,
            @PathVariable(value = 'typeCode') String typeCode
    ) {
        api.svcs.collections[collection].first.getTypeForSuffix(typeCode)
    }

    @RequestMapping(value = '{collection}', method = RequestMethod.GET)
    List<DocType> listDocType(
            @PathVariable(value = 'collection') String collection
    ) {
        api.svcs.collections[collection].first.getTypeList()
    }

    @RequestMapping(method = RequestMethod.GET)
    List<String> listCollections() {
        api.svcs.collections.keySet() as List<String>
    }


}
