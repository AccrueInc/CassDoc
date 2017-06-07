package cassdoc.springmvc

import cassdoc.CassdocAPI
import cassdoc.CommandExecServices
import cassdoc.IndexConfigurationService
import cassdoc.TypeConfigurationService
import cassdoc.springmvc.controller.APIController
import drv.cassdriver.DriverWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

// this magic seems to work...
// this tests to see if the spring boot webapp starts up...
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = [ APIController, CassdocAPI, CommandExecServices, TypeConfigurationService, IndexConfigurationService, DriverWrapper]
)
class HelloWorldIntegrationSpec extends Specification {

    @LocalServerPort
    String port

    @Autowired
    TestRestTemplate restTemplate

    void 'test helloworld status'() {

        when:
        String response = restTemplate.getForObject("http://localhost:$port/cassdoc", String)

        then:
        response?.contains('webappStatus')
    }
}
