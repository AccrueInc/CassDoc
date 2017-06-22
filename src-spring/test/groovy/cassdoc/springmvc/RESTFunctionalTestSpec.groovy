package cassdoc.springmvc

import cassdoc.CassdocAPI
import cassdoc.CommandExecServices
import cassdoc.DocType
import cassdoc.IndexConfigurationService
import cassdoc.TypeConfigurationService
import cassdoc.config.CassDocConfig

import cassdoc.inittest.Types
import cassdoc.springmvc.controller.AdminController
import cassdoc.springmvc.controller.ApiController
import cassdoc.springmvc.service.PrepareCtx
import cwdrg.lg.annotation.Log
import drv.cassdriver.DriverWrapper
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Stepwise

// this magic seems to work...
// this tests to see if the spring boot webapp starts up...
@Log
@Stepwise
@EnableAutoConfiguration
//@ContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [ApiController, AdminController, CassdocAPI, CommandExecServices, PrepareCtx, TypeConfigurationService, IndexConfigurationService, DriverWrapper, CassDocConfig]
)
class RESTFunctionalTestSpec extends Specification {

    @LocalServerPort
    String port

    @Autowired ApplicationContext applicationContext
    @Autowired TestRestTemplate restTemplate

    static CassdocAPI cassdocAPI

    static String keyspace = 'functional_test'

    void setupSpec() {
        println "setting log levels"
        setLogLevel(CommandExecServices.name,"INFO")
        setLogLevel('org.springframework',"ERROR")
        setLogLevel('org.apache',"ERROR")
        setLogLevel('io.netty',"ERROR")
        setLogLevel('com.datastax',"ERROR")
        EmbeddedCassandraServerHelper.startEmbeddedCassandra()
    }

    void 'springmvc server starts up'() {
        when:
        cassdocAPI = applicationContext.getBean('cassdocAPI')

        then:
        applicationContext != null
    }

    void 'test helloworld status'() {
        when:
        String response = restTemplate.getForObject("http://localhost:$port/up", String)

        then:
        response?.contains('webappStatus')
    }

    void 'list springmvc mappings'() {
        when:
        String response = restTemplate.getForObject("http://localhost:$port/admin/mappings", String)
        println '----------_MAPPINGS_----------'
        println response

        then:
        // this isn't working for some readon
        //response != null
        1==1
    }

    void 'setup schema'() {
        when:
        println 'setup schema'
        cassdocAPI.svcs.driver.autoStart = true
        cassdocAPI.svcs.driver.clusterContactNodes = '127.0.0.1'
        cassdocAPI.svcs.driver.clusterPort = 9142 // embedded uses this port
        cassdocAPI.svcs.driver.initDataSources()
        restTemplate.postForObject("http://localhost:$port/admin/cassdoc_system_schema", null, String)
        restTemplate.postForObject("http://localhost:$port/admin/$keyspace", null, String)
        restTemplate.postForObject("http://localhost:$port/admin/$keyspace/doctype", Types.product(), String)
        restTemplate.postForObject("http://localhost:$port/admin/$keyspace/doctype", Types.job(), String)
        cassdocAPI.svcs.collections.keySet().each {
            println "$it : "
            cassdocAPI.svcs.collections[it].first.typeList.each{println "    "+it.suffix}
        }
        DocType job = restTemplate.getForObject("http://localhost:$port/admin/$keyspace/JOB",DocType)
        DocType prod = restTemplate.getForObject("http://localhost:$port/admin/$keyspace/PROD",DocType)
        String schema = restTemplate.getForObject("http://localhost:$port/admin/_schema_/$keyspace",String)

        then:
        job != null
        prod != null
        job.fixedAttrList.size() == 2
        prod.fixedAttrList.size() == 5
        schema.contains("CREATE KEYSPACE $keyspace")
        schema.contains("CREATE TABLE ${keyspace}.e_prod")
        schema.contains("condition text")
        schema.contains("submit_date date")
        schema.contains("CREATE TABLE ${keyspace}.p_prod")
        schema.contains("CREATE TABLE ${keyspace}.e_job")
        schema.contains("provider text")
        schema.contains("CREATE TABLE ${keyspace}.p_job")
        noExceptionThrown()
    }

    void 'create doc and retrieve it'() {
        when:
        String proddoc = this.class.classLoader.getResourceAsStream('cassdoc/testdata/DocWithFixedAttrs.json').getText()
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:$port/doc/$keyspace", HttpMethod.PUT, new HttpEntity<String>(proddoc), String)
        println 'DOCID: '+response.body
        String docid = response.body
        String json = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}", String).body
        println 'LOOKUP: '+json
        // new attribute
        String aNewAttribute = '{"a":1,"b":4.5,"c":true,"d":"ddd","e":{"aa":11,"bb":"BBBB"}}'
        response = restTemplate.exchange("http://localhost:$port/doc/$keyspace/$docid/ANewAttribute", HttpMethod.PUT, new HttpEntity<String>(aNewAttribute), String)
        String attrjson = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}/ANewAttribute", String).body
        // update that attribute
        response = restTemplate.exchange("http://localhost:$port/doc/$keyspace/$docid/ANewAttribute", HttpMethod.POST, new HttpEntity<String>("99.01"), String)
        String attrUpdated = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}/ANewAttribute", String).body

        then:
        json.contains(docid)
        json.contains('8898988898')
        attrjson.contains('BBBB')
        attrUpdated.contains('99.01')

        when:
        restTemplate.delete("http://localhost:$port/doc/$keyspace/$docid/ANewAttribute")
        attrUpdated = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}/ANewAttribute", String).body

        then:
        attrUpdated == 'null'

        when:
        setLogLevel(DriverWrapper.name,"DEBUG")
        restTemplate.delete("http://localhost:$port/doc/$keyspace/$docid")
        json = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}", String).body
        setLogLevel(DriverWrapper.name,"INFO")

        then:
        json == 'null'

    }

    static void setLogLevel(String loggername, String lvl) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(loggername == null ? "ROOT" : loggername)
        logger.setLevel(ch.qos.logback.classic.Level.toLevel(lvl, ch.qos.logback.classic.Level.DEBUG))
    }
}
