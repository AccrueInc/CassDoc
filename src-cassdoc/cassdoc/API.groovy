package cassdoc

import groovy.transform.CompileStatic

import org.apache.commons.lang3.StringEscapeUtils
import org.springframework.beans.factory.annotation.Autowired

import cassdoc.commands.mutate.UpdAttrMetadata
import cassdoc.commands.mutate.UpdDocMetadata
import cassdoc.commands.mutate.UpdRelMetadata
import cassdoc.commands.retrieve.GetAttrCmd
import cassdoc.commands.retrieve.GetAttrRCH
import cassdoc.commands.retrieve.GetDocAttrs
import cassdoc.commands.retrieve.GetDocAttrsRCH
import cassdoc.commands.retrieve.QueryToListOfStrArr
import cassdoc.operations.CreateOperations
import cassdoc.operations.DeleteOperations
import cassdoc.operations.RetrievalOperations
import cassdoc.operations.UpdateOperations

import com.jayway.jsonpath.JsonPath

import cwdrg.lg.annotation.Log
import cwdrg.util.json.JSONUtil


// TODO:
// - more paxos
// - more graph + test graph
// - search operations


@Log
@CompileStatic
class API {

  @Autowired
  CommandExecServices svcs;


  // ---- Streaming Parse Read Operations

  /**
   * Get a document attribute, with no recursion or document parsing introspection for subdocuments.
   * 
   * Non-streaming version.
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @return
   */
  String getSimpleAttr(OperationContext opctx, Detail detail, String docUUID, String attr)
  {

    StringWriter writer = new StringWriter()
    getSimpleAttr(opctx,detail,docUUID,attr,writer)
    String toStr = writer.toString()
    log.dbg( "OPGetAttrSimple_return"+toStr, null)
    return toStr
  }

  /**
   * Get a document attribute, with no recursion or document parsing introspection for subdocuments.
   * 
   * Streaming version.
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param writer
   */
  void getSimpleAttr (OperationContext opctx, Detail detail, String docUUID, String attr, Writer writer)
  {
    log.inf("OPGetAttrSimple_top :: $docUUID $attr",null)

    GetAttrCmd cmd = new GetAttrCmd(docUUID:docUUID, attrName:attr)
    GetAttrRCH rch = cmd.queryCassandra(svcs, opctx, detail)

    if (rch.valType == 'S') {
      writer << '"' << StringEscapeUtils.escapeJson(rch.data) << '"'
    } else {
      writer << rch.data
    }
  }

  Object deserializeSimpleAttr(OperationContext opctx, Detail detail, String docUUID, String attr)
  {

    GetAttrCmd cmd = new GetAttrCmd(docUUID:docUUID, attrName:attr)
    GetAttrRCH rch = cmd.queryCassandra(svcs, opctx, detail)
    if (rch.data == null) {
      return null
    }
    if (rch.valType == 'O') {
      return JSONUtil.deserializeMap(rch.data)
    }
    if (rch.valType == 'A') {
      return JSONUtil.deserializeList(rch.data)
    }
    if (rch.valType == 'S') {
      return rch.data
    }
    if (rch.valType == 'B') {
      return Boolean.parseBoolean(rch.data)
    }
    if (rch.valType == 'I') {
      return new BigInteger(rch.data)
    }
    if (rch.valType == 'D') {
      return new BigDecimal(rch.data)
    }
    return null
  }

  /**
   * Get a document, with no recursion for subdocuments or other parsing of the document's content.
   * 
   * Non-streaming version
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @return
   */
  String getSimpleDoc(OperationContext opctx, Detail detail, String docUUID)
  {
    StringWriter writer = new StringWriter()
    getSimpleDoc(opctx,detail,docUUID,writer)
    return writer.toString()
  }

  /**
   * Get a document, with no recursion for subdocuments or other parsing of the document's content.
   * 
   * Streaming version
   * 
   * TODO: the attribute pulls are not streaming based on a rolling result set however....fix this
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param writer
   */
  void getSimpleDoc (OperationContext opctx, Detail detail, String docUUID, Writer writer)
  {
    writer << '{"_id":"'<<docUUID<<'"'
    GetDocAttrs cmd = new GetDocAttrs(docUUID:docUUID)
    GetDocAttrsRCH rch = cmd.queryCassandra(svcs,opctx,detail)
    for (Object[] attr : rch.attrs) {
      writer << ',"'<< StringEscapeUtils.escapeJson((String)attr[0]) << '":'
      if (attr[1] == 'S') {
        writer << '"' << StringEscapeUtils.escapeJson((String)attr[2]) << '"'
      } else {
        writer << attr[2]
      }
    }
    writer << '}'
  }

  /**
   * Get a document, with parsing of document content for recursive subdocument pulls if inidcated by detail
   * 
   * Non-streaming version
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @return
   */
  String getDoc(OperationContext opctx, Detail detail, String docUUID) {
    StringWriter writer = new StringWriter()
    RetrievalOperations.getSingleDoc(svcs,opctx,detail,docUUID,writer,true)
    return writer.toString()
  }

  /**
   * Get a document, with parsing of document content for recursive subdocument pulls if inidcated by detail
   * 
   * Streaming version
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param writer
   */
  void getDoc(OperationContext opctx, Detail detail, String docUUID, Writer writer) {
    RetrievalOperations.getSingleDoc(svcs,opctx,detail,docUUID,writer,true)
  }

  /**
   * Get a document deserialized into a Map
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @return
   */
  Map<String,Object> deserializeDoc(OperationContext opctx, Detail detail, String docUUID) {
    Map map = RetrievalOperations.deserializeSingleDoc(svcs, opctx, detail, docUUID, true)
    return map
  }

  // TODO: JsonPath for getDoc

  /**
   * Get a document attribute, with parsing of attribute's content for recursive subdocument pulls if inidcated by detail
   * 
   * Non-streaming version
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @return
   */
  String getAttr(OperationContext opctx, Detail detail, String docUUID, String attr) {
    StringWriter writer = new StringWriter()
    RetrievalOperations.getAttr(svcs,opctx,detail,docUUID,attr,writer)
    return writer.toString()
  }

  /**
   * Get a document attribute, with parsing of attribute's content for recursive subdocument pulls if inidcated by detail
   * 
   * Streaming version
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param writer
   */
  void getAttr(OperationContext opctx, Detail detail, String docUUID, String attr, Writer writer) {
    RetrievalOperations.getAttr(svcs,opctx,detail,docUUID,attr,writer)
  }

  /**
   * Get a document attribute, with parsing of attribute's content for recursive subdocument pulls if inidcated by detail
   *
   * Non-streaming version
   *
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @return
   */
  Object deserializeAttr(OperationContext opctx, Detail detail, String docUUID, String attr) {
    Object attrVal =  RetrievalOperations.deserializeAttr(svcs, opctx, detail, docUUID, attr)
    return attrVal
  }


  // TODO: jsonpath - there appear to be mutation abilities as well
  // TODO: jsonpath - avoid full serialization step... would require a custom json

  /**
   * execute the provided jsonpath expression against the json representation of the requested document
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param jsonPath
   * @return
   */
  String getDocJsonPath(OperationContext opctx, Detail detail, String docUUID, String jsonPath) {
    StringWriter writer = new StringWriter()
    RetrievalOperations.getSingleDoc(svcs,opctx,detail,docUUID,writer,true)
    String json = writer.toString()
    JsonPath pathexpr = JsonPath.compile(jsonPath)
    return JsonPath.parse(json).read(pathexpr).toString()
  }

  /**
   * execute the provided jsonpath expression against the json value of the attribute
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param jsonPath
   * @return
   */
  String getAttrJsonPath(OperationContext opctx, Detail detail, String docUUID, String attr, String jsonPath) {

    //http://blog.ostermiller.org/convert-a-java-writer-to-a-reader/

    StringWriter writer = new StringWriter()
    RetrievalOperations.getAttr(svcs,opctx,detail,docUUID,attr,writer)
    String json =  writer.toString()
    JsonPath pathexpr = JsonPath.compile(jsonPath)
    return JsonPath.parse(json).read(pathexpr).toString()
  }

  // TODO: jsonpath for getAttr
  void getAttrJsonPath(OperationContext opctx, Detail detail, String docUUID, String attr, String jsonpath, Writer writer) {
    // TODO streaming version
  }

  /**
   * Delete document: cascading deletes of subdocuments are controlled by detail. In cassandra this should delete the entire row and it's relations
   * 
   * Synchronous (TODO: asynchronous cascade api)
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   */
  void delDoc(OperationContext opctx, Detail detail, String docUUID)
  {
    DeleteOperations.deleteDoc(svcs, opctx, detail, docUUID)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail)
  }

  /**
   * Delete document attribute: cascading deletes of the attribute's subdocuments are controlled by detail. In cassandra this deletes a column key within a row
   * 
   * Synchronous (TODO: asynchronous cascade api call)
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   */
  void delAttr(OperationContext opctx, Detail detail, String docUUID, String attr)
  {
    DeleteOperations.deleteAttr(svcs, opctx, detail, docUUID, attr, false)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail)
  }

  // ---- Streaming Parse Write Operations

  /**
   * Create new document from the provided JSON string. 
   * 
   * The _id is autogenerated based on the indicated document type. If a fully pregenerated _id value is provided in the JSON, it is discarded since that is unreliable / insecure
   * 
   * @param opctx
   * @param detail
   * @param json
   * @return
   */
  public String newDoc(OperationContext opctx, Detail detail, String json) {
    String newid =  CreateOperations.newDoc(svcs,opctx,detail,json)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail)
    return newid
  }

  /**
   * Create new document, with Cassandra conditional updates to ensure the generated _id is not already present.
   *
   * The _id is autogenerated based on the indicated document type. If a fully pregenerated _id value is provided in the JSON, it is discarded since that is unreliable / insecure
   * 
   * TODO: ???allow pregenerated _id in this case since we verify???
   *   
   * @param opctx
   * @param detail
   * @param json
   * @return
   */
  public String newDocPAXOS(OperationContext opctx, Detail detail, String json) {
    //return CreateOperations.newDoc(svcs,opctx,detail,json)
  }

  /**
   * A streaming multiple document call. Input stream is a json array of documents (initiated by a root '[' start array character and ended by the matching ']' end array character.
   * 
   * Documents are returned in a streaming JSON array of strings corresponding to the generated IDs.
   * 
   * @param opctx
   * @param detail
   * @param jsonListReader
   * @param jsonList
   */
  public void newDocList(OperationContext opctx, Detail detail, Reader jsonListReader, Writer jsonIDList)
  {
    CreateOperations.newDocStream(svcs, opctx, detail, jsonListReader, jsonIDList)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations

  }

  /**
   * Add a new attribute to a document. ?conditional/preverification? 
   * 
   * Synchronous (TODO: async)
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param json
   */
  public void newAttr(OperationContext opctx, Detail detail, String docUUID, String attr, String json)
  {
    CreateOperations.newAttr(svcs,opctx,detail,docUUID,attr,json)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations
  }

  /**
   * Add a new attribute to a document, verifying that the attribute does not already exist.
   * 
   * Synchronous TODO: async
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param json
   */
  public void newAttrPAXOS(OperationContext opctx, Detail detail, String docUUID, String attr, String json)
  {
    //CreateOperations.newAttr(svcs,opctx,detail,docUUID,attr,json)
  }


  // ---- Some Update operations

  /**
   * Update the attribute of a document using PAXOS on the document. This requires that the invoker
   * has the expected version/checkvalue UUID already from a previous retrieval. Note that PAXOS is 
   * not employed currently for cascading deletes/updates that result 
   * 
   * Synchronous
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param json
   * @param checkVal
   */
  public void updateAttrPAXOS(OperationContext opctx, Detail detail, String docUUID, String attr, String json, UUID checkVal)
  {
    opctx.paxosGatekeeperUpdateID = ["P", docUUID] as String[]
    UpdateOperations.updateAttrPAXOS(svcs,opctx,detail,docUUID,attr,json,checkVal)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations
  }

  /**
   * Update the attribute of a document, using only detail-indicated consistency indicators and NOT using PAXOS. 
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param json
   */
  public void updateAttr(OperationContext opctx, Detail detail, String docUUID, String attr, String json)
  {
    UpdateOperations.updateAttr(svcs,opctx,detail,docUUID,attr,json)
    if (opctx.executionMode == "batch")     opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations
  }

  /**
   * This updates a document with mixed creation of new subdocuments as well as preserving indicated extant ids. 
   * Subdocuments with _ids that are blank / unformed are created as new documents
   * Subdocuments with fully formed _ids are assumed to currently exist
   * Existing subdocuments that aren't encountered in the overlay update are removed/cleanedup
   * 
   *  Synchronous
   * 
   * @param opctx
   * @param detail
   * @param docUUID
   * @param attr
   * @param json
   * @return
   */
  public Set<String> updateAttrOverlay(OperationContext opctx, Detail detail,String docUUID, String attr, String json)
  {
    UpdateOperations.updateAttrOverlay(svcs, opctx, detail, docUUID, attr, json)
    if (opctx.executionMode == "batch") opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations
  }

  public List<Object[]> query(OperationContext opctx, Detail detail, String cql, Object[] args)
  {
    QueryToListOfStrArr cmd = new QueryToListOfStrArr(query:cql)
    if (args != null)
      cmd.initiateQuery(svcs, opctx, detail, args)
    else
      cmd.initiateQuery(svcs, opctx, detail)
    List queryresult = []
    Object[] data = null
    while (data = cmd.nextRow()) {
      queryresult.add(data)
    }
    return queryresult
  }

  public String getDocMetadata(OperationContext opctx, Detail detail, String docUUID)
  {
    Writer writer = new StringWriter()
    String metaid = RetrievalOperations.getDocMetadataUUID(svcs, opctx, detail, docUUID)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
    return writer.toString()
  }

  public void getDocMetadata(OperationContext opctx, Detail detail, String docUUID, Writer writer)
  {
    String metaid = RetrievalOperations.getDocMetadataUUID(svcs, opctx, detail, docUUID)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
  }

  public Map<String,Object> deserializeDocMetadata(OperationContext opctx, Detail detail, String docUUID)
  {
    String metaid = RetrievalOperations.getDocMetadataUUID(svcs, opctx, detail, docUUID)
    Map doc = RetrievalOperations.deserializeSingleDoc(svcs, opctx, detail, metaid, true)
    return doc
  }


  public String getAttrMetadata(OperationContext opctx, Detail detail, String docUUID, String attr)
  {
    Writer writer = new StringWriter()
    String metaid = RetrievalOperations.getAttrMetadataUUID(svcs, opctx, detail, docUUID, attr)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
    return writer.toString()
  }

  public void getAttrMetadata(OperationContext opctx, Detail detail, String docUUID, String attr, Writer writer)
  {
    String metaid = RetrievalOperations.getAttrMetadataUUID(svcs, opctx, detail, docUUID, attr)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
  }

  public Map<String,Object> deserializeAttrMetadata(OperationContext opctx, Detail detail, String docUUID, String attr)
  {
    String metaid = RetrievalOperations.getAttrMetadataUUID(svcs, opctx, detail, docUUID, attr)
    Map doc = RetrievalOperations.deserializeSingleDoc(svcs, opctx, detail, metaid, true)
    return doc
  }


  public String getRelMetadata(OperationContext opctx, Detail detail, RelKey rel)
  {
    Writer writer = new StringWriter()
    String metaid = RetrievalOperations.getRelMetadataUUID(svcs, opctx, detail, rel)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
    return writer.toString()
  }

  public void getRelMetadata(OperationContext opctx, Detail detail, RelKey rel, Writer writer)
  {
    String metaid = RetrievalOperations.getRelMetadataUUID(svcs, opctx, detail, rel)
    RetrievalOperations.getSingleDoc(svcs, opctx, detail, metaid, writer, true)
  }

  public Map<String,Object> deserializeRelMetadata(OperationContext opctx, Detail detail, RelKey rel)
  {
    String metaid = RetrievalOperations.getRelMetadataUUID(svcs, opctx, detail, rel)
    Map doc = RetrievalOperations.deserializeSingleDoc(svcs, opctx, detail, metaid, true)
    return doc
  }

  public String docMetadataUUID(OperationContext opctx, Detail detail, String docUUID)
  {
    String metaid = RetrievalOperations.getDocMetadataUUID(svcs, opctx, detail, docUUID)
    if (metaid != null) {
      return metaid
    }
    // create new UUID for META
    Detail initDetail = new Detail()
    initDetail.writeConsistency = detail.readConsistency
    initDetail.docMetaIDMeta = true
    metaid = CreateOperations.newDoc(svcs, opctx, initDetail, '{"_id":"META"}')
    UpdDocMetadata upd = new UpdDocMetadata(docUUID:docUUID, metadataUUID: metaid)
    upd.execMutationCassandra(svcs, opctx, initDetail)
    return metaid
  }


  public String attrMetadataUUID(OperationContext opctx, Detail detail, String docUUID, String attr)
  {
    String metaid = RetrievalOperations.getAttrMetadataUUID(svcs, opctx, detail, docUUID, attr)
    if (metaid != null) {
      return metaid
    }
    // create new UUID for META
    metaid = CreateOperations.newDoc(svcs, opctx, detail, '{"_id":"META"}')
    UpdAttrMetadata upd = new UpdAttrMetadata(docUUID:docUUID,attr:attr, metadataUUID: metaid)
    upd.execMutationCassandra(svcs, opctx, detail)
    return metaid
  }

  public String relMetadataUUID(OperationContext opctx, Detail detail, RelKey rel)
  {
    String metaid = RetrievalOperations.getRelMetadataUUID(svcs, opctx, detail, rel)
    if (metaid != null) {
      return metaid
    }
    // create doc + relation
    metaid = CreateOperations.newDoc(svcs, opctx, detail, '{"_id":"META"}')
    // Update the z_md field in the e table for the doc TODO: should be paxos...
    UpdRelMetadata upd = new UpdRelMetadata(relkey:rel, metadataUUID: metaid)
    upd.execMutationCassandra(svcs, opctx, detail)
    return metaid
  }

  public void addRel(OperationContext opctx, Detail detail, Rel rel)
  {
    CreateOperations.addRel(svcs, opctx, detail, rel)
    if (opctx.executionMode == "batch")     opctx.DO(svcs, detail) // TODO: figure out this vs streaming data operations
  }

  public List<Rel> deserializeDocRels(OperationContext opctx, Detail detail, String docUUID)
  {
    RetrievalOperations.deserializeDocRels(svcs,opctx,detail,docUUID)
  }



  /**
   * Searches for large distributed databases should be done via indexes, that are registered/known to the engine.
   *
   *  Index types: secondary indexes (cassandra maintained), materialized views (cass maintained), manual value indexes, external indexes (B+ in relational store)
   *
   *  TODO: sorter
   *
   * @param indexName
   * @return
   */
  public Iterator<Map> searchIndex(OperationContext opctx, Detail detail, String indexName, List searchCriteria, List<SearchFilter> filters)
  {
    Index idx = svcs.idxSvc.getIndex(indexName)
    Iterator<Map> iterator = idx.searchIndex(svcs, opctx, detail, searchCriteria)
    return iterator
  }


  /**
   * Searches for large distributed databases should be done via indexes, that are registered/known to the engine.
   *
   *  Index types: secondary indexes (cassandra maintained), materialized views (cass maintained), manual value indexes, external indexes (B+ in relational store)
   *  
   *  TODO: sorter
   *
   * @param indexName
   * @return
   */
  public void searchIndex(OperationContext opctx, Detail detail, String indexName, List searchCriteria, List<SearchFilter> filters, Writer searchResultsWriter)
  {
    Iterator<Map> iterator = searchIndex(opctx,detail,indexName, searchCriteria, filters)
    searchResultsWriter << "["
    while (iterator.hasNext()) {
      Map doc = iterator.next()
      searchResultsWriter << "{" << '"_id":'
      searchResultsWriter << doc._id
      for (Map.Entry e : doc.entrySet()) {
        if (e.key != "_id") {
          searchResultsWriter << ',"' << StringEscapeUtils.escapeJson(e.key.toString()) << '":'
          searchResultsWriter << JSONUtil.serialize(e.value)
        }
      }
      searchResultsWriter << "}"
    }
  }






}

// PAXOS...
// - paxos updates can be batched if rowkey/table aka partition is shared:
// ... each unsafe update is a random negative version number
// ... paxos will update with a positive incremental
// ... store last paxos version too, so paxos re-resumes when overwriting unsafe


