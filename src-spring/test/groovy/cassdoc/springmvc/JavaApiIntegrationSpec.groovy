package cassdoc.springmvc

import cassdoc.CassdocAPI
import cassdoc.CommandExecServices
import cassdoc.IndexConfigurationService
import cassdoc.TypeConfigurationService
import cassdoc.springmvc.controller.APIController
import drv.cassdriver.DriverWrapper
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

// webapp + cassandra embedded
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [ APIController, CassdocAPI, CommandExecServices, TypeConfigurationService, IndexConfigurationService, DriverWrapper]
)
class JavaApiIntegrationSpec extends Specification {

    void setupSpec() {

        EmbeddedCassandraServerHelper.startEmbeddedCassandra()

    }

    void 'check cassandra status'() {
        given:
        DriverWrapper wrapper = new DriverWrapper(clusterContactNodes: '127.0.0.1', clusterPort: 9142, autoStart: true)

        when:
        Set<String> ks = wrapper.keyspaces

        then:
        ks.toString().contains("system_")
    }

    void cleanupSpec() {
        //EmbeddedCassandraServerHelper.stopEmbeddedCassandra()
    }
}

// unneeded annotations (did not work, PITA, or didn't encounter the same errors), but for reference:
//@CassandraDataSet(value = 'cql/setup.cql', keyspace = 'integration_test')
//@EmbeddedCassandra //configuration = "cu-cassandra.yaml", clusterName = "Test Cluster", host = "127.0.0.1", port = 9142)
// may need: https://stackoverflow.com/questions/33840156/how-can-i-start-an-embedded-cassandra-server-before-loading-the-spring-context
//@TestExecutionListeners(listeners = [ CassandraUnitDependencyInjectionTestExecutionListener, CassandraUnitTestExecutionListener, DependencyInjectionTestExecutionListener.class ])
