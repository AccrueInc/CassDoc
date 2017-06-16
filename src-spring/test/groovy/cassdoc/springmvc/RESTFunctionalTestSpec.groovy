package cassdoc.springmvc

import cassdoc.CassdocAPI
import cassdoc.CommandExecServices
import cassdoc.IndexConfigurationService
import cassdoc.TypeConfigurationService
import cassdoc.config.CassDocConfig
import cassdoc.inittest.Types
import cassdoc.springmvc.controller.AdminController
import cassdoc.springmvc.controller.ApiController
import cassdoc.springmvc.service.PrepareCtx
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
        EmbeddedCassandraServerHelper.startEmbeddedCassandra()
        setLogLevel(CommandExecServices.name,"INFO")

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
        response != null
    }

    void 'setup schema'() {
        when:
        println 'setup schema'
        String response

        cassdocAPI.svcs.driver.autoStart = true
        cassdocAPI.svcs.driver.clusterContactNodes = '127.0.0.1'
        cassdocAPI.svcs.driver.clusterPort = 9142 // embedded uses this port
        cassdocAPI.svcs.driver.initDataSources()

        response = restTemplate.postForObject("http://localhost:$port/admin/cassdoc_system_schema", null, String)
        println response
        response = restTemplate.postForObject("http://localhost:$port/admin/$keyspace", null, String)
        println response
        response = restTemplate.postForObject("http://localhost:$port/admin/$keyspace/doctype", Types.product(), String)
        println response
        response = restTemplate.postForObject("http://localhost:$port/admin/$keyspace/doctype", Types.job(), String)
        println response
        cassdocAPI.svcs.collections.keySet().each {
            println "$it : "
            cassdocAPI.svcs.collections[it].first.typeList.each{println "    "+it.suffix}
        }

        then:
        cassdocAPI.svcs.collections != null
        noExceptionThrown()
    }

    void 'create doc and retrieve it'() {
        when:
        String proddoc = this.class.classLoader.getResourceAsStream('cassdoc/testdata/DocWithFixedAttrs.json').getText()
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:$port/doc/$keyspace", HttpMethod.PUT, new HttpEntity<String>(proddoc), String)
        println 'DOCID: '+response.body
        String docid = response.body
        response = restTemplate.getForEntity("http://localhost:$port/doc/$keyspace/${docid}", String).body
        println 'LOOKUP: '+response

        then:
        cassdocAPI.svcs.collections != [:]
        response.contains(docid)
        response.contains('8898988898')
    }

    static void setLogLevel(String loggername, String lvl) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(loggername == null ? "ROOT" : loggername)
        logger.setLevel(ch.qos.logback.classic.Level.toLevel(lvl, ch.qos.logback.classic.Level.DEBUG))
    }
}
