package cassdoc.tests;

import static org.junit.Assert.*

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import cassdoc.API
import cassdoc.Detail
import cassdoc.OperationContext
import cassdoc.Rel
import cassdoc.testinit.TestServiceInitializer
import cwdrg.util.json.JSONUtil

/**
 * Disclaimer: these tests are executed against a single-node local cassandra. As such, many 
 * issues with eventual consistency will not be evident here. This set of tests is for checking
 * the essential operational logic works.
 * 
 * Where this would become problematic / race condition vulnerable is in the numerous instances 
 * where we create something and then immediately begin pulling it. Granted, our detail levels 
 * are defaulting to LOCAL_QUORUM so that should protect us ...
 * 
 * 
 * @author cowardlydragon
 *
 */

class BasicTests {
  static API api

  @BeforeClass
  static void setup() {
    println "---- BEGIN"
    api = TestServiceInitializer.initAPI()
  }

  @Test
  public void testCassIsUp() {
    Set<String> keyspaces = api.svcs.driver.getKeyspaces()
    println "keyspaces: "+keyspaces
    assertTrue(keyspaces?.size() > 0)
  }

  @Test
  public void basicDocTests() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"

    Detail detail = new Detail()

    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/SimpleDoc.json").getText()

    // create and retrieve and compare
    String newid = api.newDoc(opctx, detail, doc)
    println newid
    String json = api.getDoc(opctx, detail, newid)
    Map original = JSONUtil.deserializeMap(doc)
    // need to "match" the id key in the original doc
    original._id = newid
    Map retrieved = JSONUtil.deserializeMap(json)
    Map retrievedMap = api.deserializeDoc(opctx, detail, newid)

    println "Orig: "+original
    println "Retr: "+retrieved
    println "RMap: "+retrievedMap

    assertTrue(original == retrieved)
    assertTrue(original == retrievedMap)

    // remove attr
    api.delAttr(opctx, detail, newid, "hello", false)
    original.remove("hello")
    json = api.getDoc(opctx, detail, newid)
    retrieved = JSONUtil.deserializeMap(json)
    assertTrue(original == retrieved)

    // set attrs!
    api.newAttr(opctx, detail, newid, "newInt", "22", false, false)
    api.newAttr(opctx, detail, newid, "newString", '"str"', false, false)
    api.newAttr(opctx, detail, newid, "newNull", 'null', false, false)
    api.newAttr(opctx, detail, newid, "newFloat", '8.999933', false, false)
    api.newAttr(opctx, detail, newid, "newArray", '[5,10,15,"20"]', false, false)
    api.newAttr(opctx, detail, newid, "newComplex", '{"a":"aaaa","b":22,"c":null}', false, false)
    original.put("newInt", 22)
    original.put("newNull",null)
    original.put("newString", "str")
    original.put("newFloat", 8.999933)
    original.put("newArray", [5, 10, 15, "20"])
    original.put("newComplex", ["a":"aaaa","b":22,"c":null])
    json = api.getDoc(opctx, detail, newid)
    retrieved = JSONUtil.deserializeMap(json)
    retrievedMap = api.deserializeDoc(opctx, detail, newid)
    assertTrue(original == retrieved)
    assertTrue(original == retrievedMap)

    assertTrue(api.getAttr(opctx, detail, newid, "newInt") == "22")
    assertTrue(api.getAttr(opctx, detail, newid, "newString") == '"str"')
    assertTrue(api.getAttr(opctx, detail, newid, "newNull") == "null")
    assertTrue(api.getAttr(opctx, detail, newid, "newFloat") == "8.999933")
    assertTrue(api.getAttr(opctx, detail, newid, "newArray") == '[5,10,15,"20"]')
    assertTrue(api.getAttr(opctx, detail, newid, "newComplex") == '{"a":"aaaa","b":22,"c":null}')

    // test the "simple" apis which do less parsing and no recursive pulls

    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newInt") == "22")
    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newString") == '"str"')
    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newNull") == "null")
    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newFloat") == "8.999933")
    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newArray") == '[5,10,15,"20"]')
    assertTrue(api.getSimpleAttr(opctx, detail, newid, "newComplex") == '{"a":"aaaa","b":22,"c":null}')


    json = api.getSimpleDoc(opctx, detail, newid)
    retrieved = JSONUtil.deserializeMap(json)
    assertTrue(original == retrieved)
  }

  @Test
  public void childDocTests() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"

    Detail detail = new Detail()
    detail.pullChildDocs = true  // recursive pull all subdocs

    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithSubDocs.json").getText()
    // create and retrieve and compare
    String newid = api.newDoc(opctx, detail, doc)
    println newid
    String json = api.getDoc(opctx, detail, newid)
    Map original = JSONUtil.deserializeMap(doc)
    Map retrieved = JSONUtil.deserializeMap(json)
    Map retrMap = api.deserializeDoc(opctx, detail, newid)
    // since _id's are changed/created as the docs are, for comparison purposes we strip the _id fields
    Utils.stripKeys(original,"_id")
    Utils.stripKeys(retrieved,"_id")
    Utils.stripKeys(retrMap,"_id")
    println ("orig: "+original)
    println ("retr: "+retrieved)
    println ("rmap: "+retrMap)
    assertTrue(original == retrieved)
    assertTrue(original == retrMap)
  }

  @Test
  public void jsonPathTests() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"

    Detail detail = new Detail()
    detail.pullChildDocs = true  // recursive pull all subdocs

    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithSubDocs.json").getText()
    // create and retrieve and compare
    String newid = api.newDoc(opctx, detail, doc)
    println newid

    String pathexpr = '$.arrVal[1]'
    String result = api.getDocJsonPath(opctx, detail, newid, pathexpr)
    assertTrue(result == 'matey')

    result = api.getAttrJsonPath(opctx, detail, newid, "arrVal", '$[2]')
    assertTrue(result == 'arrrr')

    // TODO: more jsonpaths?

  }

  @Test
  public void testFixedAttributes() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"

    Detail detail = new Detail()

    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithFixedAttrs.json").getText()

    // create and retrieve and compare
    String newid = api.newDoc(opctx, detail, doc)
    println newid
    Map original = JSONUtil.deserializeMap(doc)

    List checkGtin = api.query(opctx, detail, "SELECT token(e),gtin FROM proto_jsonstore.e_prod where e = '$newid'")
    assertTrue(original["dbpedia:GTIN"] == checkGtin[0][1])

    checkGtin = api.query(opctx, detail, "SELECT token(e),submit_date FROM proto_jsonstore.e_prod where e = '$newid'")
    assertTrue(original["product:submitdate"] == Long.parseLong(checkGtin[0][1]))

    // TODO: test other fixed column types

  }

  /** 
   * test that the metadata get/init calls work. 
   * 
   * Once the metadata id is initialized, normal attribute get/set is done to manipulate the metadata fields/attributes.
   * 
   * I'm not sure I like these "side effect" api calls that look like gets, but I took "get" out to be sure...
   * 
   */
  @Test
  public void testMetadataInit() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"
    Detail detail = new Detail()
    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithOneSubDoc.json").getText()
    String newid = api.newDoc(opctx, detail, doc)
    Map original = JSONUtil.deserializeMap(doc)
    original._id = newid

    String docmeta = api.docMetadataUUID(opctx, detail, newid)
    String attrmeta = api.attrMetadataUUID(opctx, detail, newid, "fVal")
    String docmeta2 = api.docMetadataUUID(opctx, detail, newid)
    String attrmeta2 = api.attrMetadataUUID(opctx, detail, newid, "fVal")
    println "metaids: $docmeta $docmeta2 $attrmeta $attrmeta2"
    assertTrue(docmeta == docmeta2)
    assertTrue(attrmeta == attrmeta2)

    List<Rel> rels = api.deserializeDocRels(opctx, detail, newid)
    println "rels:" +JSONUtil.serialize(rels)
    String relmeta = api.relMetadataUUID(opctx, detail, rels[0].relKey)
    String relmeta2 = api.relMetadataUUID(opctx, detail, rels[0].relKey)
    assertTrue(relmeta == relmeta2)
  }

  @Test
  public void testDetail() {
    OperationContext opctx = new OperationContext()
    opctx.space = "proto_jsonstore"
    Detail detail = new Detail()

    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithSubDocs.json").getText()
    String newid = api.newDoc(opctx, detail, doc)
    Map original = JSONUtil.deserializeMap(doc)
    original._id = newid

    // attribute subset pull
    Detail subsetDTL = new Detail()
    subsetDTL.pullChildDocs = true
    subsetDTL.attrSubset = ["nullVal", "hello", "iVal"] as Set
    String json = api.getDoc(opctx, subsetDTL, newid)
    println "subset: "+json

    Map subsetOrig = [:]
    subsetOrig._id = newid
    subsetOrig.nullVal = original.nullVal
    subsetOrig.hello = original.hello
    subsetOrig.iVal = original.iVal
    assertTrue(subsetOrig == JSONUtil.deserializeMap(json))

    // attr exclude
    Detail exclDTL = new Detail()
    exclDTL.pullChildDocs = true
    exclDTL.attrExclude = [
      '$%^&*@#(&*#$(){}[]--09*(*()62347813420<>,.,<~`/?',
      "JustASubDoc",
      "hello",
      '&^%$HeteroArrayOfSubdocs',
      "fVal"] as Set
    json = api.getDoc(opctx, exclDTL, newid)
    println "exclude: "+json

    Map excl = JSONUtil.deserializeMap(doc)
    excl._id = newid
    excl.remove('$%^&*@#(&*#$(){}[]--09*(*()62347813420<>,.,<~`/?')
    excl.remove('JustASubDoc')
    excl.remove('hello')
    excl.remove('&^%$HeteroArrayOfSubdocs')
    excl.remove('fVal')
    assertTrue(excl == JSONUtil.deserializeMap(json))

    exclDTL.attrExclude = [
      'SingleSubDoc',
      "fVal"] as Set
    json = api.getDoc(opctx, exclDTL, newid)
    println "exclude-2: "+json
    Map excl2 = JSONUtil.deserializeMap(json)
    assertTrue(!excl2.JustASubDoc.containsKey("SingleSubDoc"))

    // a detail where special detail is applied to a single attribute (JustASubDoc)
    // such that only that attribute's subdocs are pulled
    Detail attrSpecialDTL = new Detail()
    Detail attrsubDTL = new Detail()
    attrsubDTL.pullChildDocs = true
    attrSpecialDTL.attrDetail = [:]
    attrSpecialDTL.attrDetail.JustASubDoc = attrsubDTL
    json = api.getDoc(opctx, attrSpecialDTL, newid)
    Map spcl = JSONUtil.deserializeMap(json)
    assertTrue(!spcl['&^%$HeteroArrayOfSubdocs'][0].containsKey("firstDoc"))
    assertTrue(spcl.JustASubDoc.SingleSubDoc == "yeppers")

    // detail that only applies to subdocuments of a specific type/collection
    Detail typeSpecificDTL = new Detail()
    typeSpecificDTL.pullChildDocs = true
    typeSpecificDTL.childDocSuffixDetail = [:]
    typeSpecificDTL.childDocSuffixDetail["PROD"] = new Detail()
    typeSpecificDTL.childDocSuffixDetail.PROD.docIDTimestampMeta = true
    json = api.getDoc(opctx, typeSpecificDTL, newid)
    Map typSpec = JSONUtil.deserializeMap(json)
    // assert that the JOB-specific detail did not pull the d
    println "TypeSpecific: "+json

  }

  @Test
  void testHasValueManualIndex() {

    OperationContext opctx = new OperationContext(space:"proto_jsonstore")
    Detail detail = new Detail()

    // doc with an attribute that triggers a manual index
    String doc = this.class.classLoader.getResourceAsStream("cassdoc/tests/DocWithIndexTrigger.json").getText()
    String newid = api.newDoc(opctx, detail, doc)

    println "idx trigger: "+newid

    List index = api.query(opctx, detail, "SELECT token(i1,i2,i3,k1,k2,k3),v1 from ${opctx.space}.i WHERE i1 = 'PROD' and i2 = 'IC' and i3 = 'HV' and k1 = 'AZ889__55' and k2 = '' and k3 = ''")

    assertTrue(index.find{it[1] == newid} != null)

    // deletes that have the same timestamp as the update timestamp of a cell are ignored. delete timestamp must be > update timestamp.
    // I *think* subsequent updates to a cell with the same timestamp that come in do overwrite, as in "last one wins"
    // huh, why didn't the basic tests fail??? They reuse the same opctx...
    // in that instance, we do a delete, then a select, maybe that asserts the delete.
    // maybe the groovy map compare operation isn't accurate...
    // maybe it is the complexity of the i table's keys, or the trailing ""'s in the v2/v3 fields, or that d/id are null, whereas the other delete of an attribute had cells in the cluster key
    println "NEW OPCTX"
    opctx = new OperationContext(space:"proto_jsonstore")
    api.delAttr(opctx, detail, newid, "proprietary:InternalCode", false)

    index = api.query(opctx, detail, "SELECT token(i1,i2,i3,k1,k2,k3),v1 from ${opctx.space}.i WHERE i1 = 'PROD' and i2 = 'IC' and i3 = 'HV' and k1 = 'AZ889__55' and k2 = '' and k3 = ''")
    boolean found = (index.find{it[1] == newid} != null)
    println "FoundAPI: "+found

    // code used to debug the not-deleteing deleting (because we didn't use a new OpCtx)
    //    String manprep = "DELETE FROM proto_jsonstore.i WHERE i1 = ? and i2 = ? and i3 = ? and k1 = ? and k2 = ? and k3 = ? and v1 = ? and v2 = ? and v3 = ?"
    //    Object[] args =  [
    //      'PROD',
    //      'IC',
    //      'HV',
    //      'AZ889__55',
    //      '',
    //      '',
    //      newid,
    //      '',
    //      ''] as Object[]
    //    api.svcs.driver.executeDirectUpdate("proto_jsonstore", manprep, args, "LOCAL_QUORUM", null)
    //
    //    index = api.query(opctx, detail, "SELECT token(i1,i2,i3,k1,k2,k3),v1 from ${opctx.space}.i WHERE i1 = 'PROD' and i2 = 'IC' and i3 = 'HV' and k1 = 'AZ889__55' and k2 = '' and k3 = ''")
    //
    //    boolean found2 = (index.find{it[1] == newid} != null)
    //    println "FoundManPrep: "+found2
    //
    //
    //    String man = "DELETE FROM proto_jsonstore.i WHERE i1 = 'PROD' and i2 = 'IC' and i3 = 'HV' and k1 = 'AZ889__55' and k2 = '' and k3 = '' and v1 = '$newid' and v2 = '' and v3 = ''"
    //    api.svcs.driver.executeDirectUpdate("proto_jsonstore", man, null, "LOCAL_QUORUM", null)
    //    index = api.query(opctx, detail, "SELECT token(i1,i2,i3,k1,k2,k3),v1 from ${opctx.space}.i WHERE i1 = 'PROD' and i2 = 'IC' and i3 = 'HV' and k1 = 'AZ889__55' and k2 = '' and k3 = ''")
    //
    //    boolean found3 = (index.find{it[1] == newid} != null)
    //    println "FoundMan: "+found3


    assertTrue(found == false)

  }




  // TODO: detail level

  // TODO: conditional update / paxos

  // TODO: batch mode processing, sync vs async

  // TODO: index tests




  @AfterClass
  static void teardown() {
    api.svcs.driver.destroy()
    println "---- END"

  }
}
