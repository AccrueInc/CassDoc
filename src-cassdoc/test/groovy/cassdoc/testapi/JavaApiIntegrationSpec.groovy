package cassdoc.testapi

import cassdoc.CassdocAPI
import cassdoc.Detail
import cassdoc.OperationContext
import cassdoc.inittest.JavaApiTestInitializer
import cwdrg.util.json.JSONUtil
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import spock.lang.Shared
import spock.lang.Specification

import static org.junit.Assert.assertTrue

class JavaApiIntegrationSpec extends Specification {

    static String keyspace = 'java_api_test'

    @Shared
    CassdocAPI api

    OperationContext opctx = new OperationContext(space: keyspace)
    Detail detail = new Detail()

    void setupSpec() {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra()
        api = JavaApiTestInitializer.initAPI()
        JavaApiTestInitializer.initCassandraSchema(keyspace, api.svcs.driver, api.svcs.typeSvc)
    }

    void 'check cassandra status'() {
        when:
        Set<String> ks = api.svcs.driver.keyspaces
        then:
        ks.toString().contains(keyspace)
    }

    void 'persist simple json document'() {
        given:
        String doc = this.class.classLoader.getResourceAsStream('/cassdoc/testdata/SimpleDoc.json').getText()
        when:
        Map original = JSONUtil.deserializeMap(doc)
        String newid = api.newDoc(opctx, detail, doc)
        // need to "match" the id key in the original doc so we can do compares
        original._id = newid
        String json = api.getDoc(opctx, detail, newid)
        Map retrieved = JSONUtil.deserializeMap(json)
        Map retrievedMap = api.deserializeDoc(opctx, detail, newid)
        then:
        original == retrieved
        original == retrievedMap
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
