package cassdoc.testinit

import cassdoc.API
import cassdoc.CommandExecServices
import cassdoc.DocType
import cassdoc.FixedAttr
import cassdoc.ManualIndex
import cassdoc.TypeConfigurationService
import cassdoc.commands.mutate.cassandra.CassandraMutationCommands;
import cassdoc.commands.retrieve.cassandra.CassandraRetrievalCommands

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider

import drv.cassdriver.DriverWrapper


// ------------------------ DOCTYPES ------------------------

/**
 * Define the known document types, indexes, fixed attributes, etc
 * 
 * @author cowardlydragon
 *
 */
class Types {

  static DocType product() {
    DocType prod = new DocType(uri:"cassdoc:test:product",suffix:"PROD")

    FixedAttr cond = new FixedAttr(sourceAttr:"product:condition",colname:"condition",coltype:"text")
    FixedAttr gtin = new FixedAttr(sourceAttr:"dbpedia:GTIN",colname:"gtin",coltype:"text")
    FixedAttr sku = new FixedAttr(sourceAttr:"dbpedia:SKU",colname:"sku",coltype:"text")
    FixedAttr source = new FixedAttr(sourceAttr:"product:provider",colname:"provider",coltype:"text")
    FixedAttr submit = new FixedAttr(sourceAttr:"product:submitdate",colname:"submit_date",coltype:"date")

    prod.fixedAttrList = [
      cond,
      gtin,
      sku,
      source,
      submit
    ]

    ManualIndex ICIndex = new ManualIndex(indexType:"HAS_VALUE",indexRef:"InternalCode",indexCodes:["PROD", "IC", "HV"],keyAttrs:["proprietary:InternalCode"])

    prod.manualIndexList = [ICIndex]

    return prod
  }

  static DocType job() {
    DocType job = new DocType(uri:"urn:cassdoc:test:job",suffix:"JOB")

    FixedAttr source = new FixedAttr(sourceAttr:"bybuy:job:provider",colname:"provider",coltype:"text")
    FixedAttr submit = new FixedAttr(sourceAttr:"bybuy:job:submitdate",colname:"submit_date",coltype:"date")

    job.fixedAttrList = [source, submit]
    return job
  }

  static DocType meta() {
    DocType meta = new DocType(uri:"urn:meta",suffix:"META")
    meta.fixedAttrList = []
    return meta
  }
}


// ------------------------ JACKSON-INIT ------------------------

/**
 * Initializes jsonpath to use jackson instead of json-smart
 * 
 * @author cowardlydragon
 *
 */
class JsonPathJacksonInitializer implements Configuration.Defaults {

  private final JsonProvider jsonProvider = new JacksonJsonProvider();
  private final MappingProvider mappingProvider = new JacksonMappingProvider();

  @Override
  public JsonProvider jsonProvider() {
    return jsonProvider;
  }

  @Override
  public MappingProvider mappingProvider() {
    return mappingProvider;
  }

  @Override
  public Set<Option> options() {
    return EnumSet.noneOf(Option.class);
  }


  static void initJsonPathToJackson() {
    Configuration.setDefaults(new JsonPathJacksonInitializer())
  }
}


// ------------------------ SERVICE-INIT ------------------------


/**
 * Main test server object graph coordinator
 * 
 * @author cowardlydragon
 *
 */
class TestServiceInitializer {

  static TypeConfigurationService initTypeSvc() {
    TypeConfigurationService typeSvc = new TypeConfigurationService()
    typeSvc.typeList = [
      Types.product(),
      Types.job(),
      Types.meta()
    ]
    return typeSvc
  }

  static DriverWrapper initDrv() {
    DriverWrapper drv = new DriverWrapper()
    drv.autoStart = true
    drv.clusterContactNodes = "127.0.0.1"
    drv.initDataSources()
    return drv
  }

  static CommandExecServices initCES() {
    JsonPathJacksonInitializer.initJsonPathToJackson()
    return new CommandExecServices(driver:initDrv(),typeSvc:initTypeSvc(),retrievals:new CassandraRetrievalCommands(),mutations:new CassandraMutationCommands())
  }

  static API initAPI() {
    API api = new API(svcs:initCES())
    return api
  }
}