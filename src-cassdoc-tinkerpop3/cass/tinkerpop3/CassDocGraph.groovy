package cass.tinkerpop3;

import org.apache.commons.configuration.Configuration
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Graph.Exceptions
import org.apache.tinkerpop.gremlin.structure.Graph.Variables

import cassdoc.API
import cwdrg.lg.annotation.Log

@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_PERFORMANCE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_PERFORMANCE)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_PROCESS_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_ENVIRONMENT)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_ENVIRONMENT_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_GROOVY_ENVIRONMENT_PERFORMANCE)
@Log
public class CassDocGraph implements Graph {
  String space;
  API    cassDocAPI;


  @Override
  public Vertex addVertex(Object... keyValues) {
    log.dbg("CassDoc: addVertex invoked")
    throw Graph.Exceptions.vertexAdditionsNotSupported()
  }


  @Override
  public void close() throws Exception {
    // close driver???
    // ignore since it is read only?
  }


  /**
   * Neo4j doesn't implement this...
   * 
   * http://tinkerpop.apache.org/docs/3.0.1-incubating/#graphcomputer
   */  
  @Override
  public GraphComputer compute() throws IllegalArgumentException {
    throw Exceptions.graphComputerNotSupported()
  }


  @Override
  public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
    throw Exceptions.graphComputerNotSupported()
  }


  @Override
  public Configuration configuration() {
    // ??? get types ??? keyspaces ???
    return null;
  }


  @Override
  public Iterator<Edge> edges(Object... edgeIds) {
    // edgeId: space, RelKey tuple
    cassDocAPI.getRels(edgeIds)
    // construct Edge, and append to iterator...

  }


  @Override
  public Transaction tx() {
    throw Exceptions.transactionsNotSupported();
  }


  @Override
  public Variables variables() {
    // Graph keyspace???
    return null;
  }


  @Override
  public Iterator<Vertex> vertices(Object... vertexIds) {
    // vertexID: space,id tuple
    log.dbg("CassDoc: get vertices for Ids "+vertexIds,null,null)
    cassDocAPI.getDocIds(vertexIds)
  }
}

