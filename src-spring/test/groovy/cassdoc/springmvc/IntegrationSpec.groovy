package cassdoc.springmvc

import cassdoc.CassdocAPI
import cassdoc.CommandExecServices
import cassdoc.IndexConfigurationService
import cassdoc.TypeConfigurationService
import cassdoc.springmvc.controller.APIController
import drv.cassdriver.DriverWrapper
import org.cassandraunit.spring.CassandraDataSet
import org.cassandraunit.spring.EmbeddedCassandra
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [ APIController, CassdocAPI, CommandExecServices, TypeConfigurationService, IndexConfigurationService, DriverWrapper]
)
//@CassandraDataSet(value = 'cql/setup.cql', keyspace = 'integration_test')
//@EmbeddedCassandra //configuration = "cu-cassandra.yaml", clusterName = "Test Cluster", host = "127.0.0.1", port = 9142)
// may need: https://stackoverflow.com/questions/33840156/how-can-i-start-an-embedded-cassandra-server-before-loading-the-spring-context
//@TestExecutionListeners(listeners = [ CassandraUnitDependencyInjectionTestExecutionListener, CassandraUnitTestExecutionListener, DependencyInjectionTestExecutionListener.class ])
class IntegrationSpec extends Specification {

    void setupSpec() {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    }

    void 'check status'() {
        given:
        DriverWrapper wrapper = new DriverWrapper(clusterContactNodes: '127.0.0.1', clusterPort: 9142, autoStart: true)

        when:
        println "keyspaces: ${wrapper.keyspaces}"
        println "wooo!"

        then:
        true == true

    }

    void cleanupSpec() {
        //EmbeddedCassandraServerHelper.stopEmbeddedCassandra()
    }

}