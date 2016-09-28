package cassdoc

import groovy.transform.CompileStatic

import org.springframework.beans.factory.annotation.Autowired

import com.fasterxml.jackson.core.JsonFactory

import drv.cassdriver.DriverWrapper

@CompileStatic
class CommandExecServices {

  String idField = "_id";
  // there should only be one type of database per service. If you want to support multiple databases,
  // spin up a service instance per database, and route based on space or url or similar scheme
  String dbType = "cassandra" // "JDBC" // "Dynamo"

  @Autowired
  DriverWrapper driver


  JsonFactory jsonFactory = new JsonFactory();

  @Autowired
  TypeConfigurationService typeSvc

  @Autowired
  IndexConfigurationService idxSvc


  // TODO: service-ify and autowire
}
