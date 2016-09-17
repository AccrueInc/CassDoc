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
import cassdoc.Detail
import cassdoc.OperationContext
import cassdoc.Rel
import cassdoc.RelKey
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
    log.dbg("CassDocGraph: addVertex invoked: NOT SUPPORTED: use CassDoc API methods to add a new document/vertex")
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
    OperationContext opctx = new OperationContext(space:space)
    Detail detail = new Detail()
    List edges = []
    // TODO: spawn thread for streaming object iterator rather than do full list construction
    for (Object o : edgeIds) {
      Rel rel = cassDocAPI.deserializeRel(opctx, detail, (RelKey)o)
      CassDocEdge edge = new CassDocEdge(rel:rel,cassDocGraph:this)
      edges.add(edge)
    }
    return edges.iterator()
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
    OperationContext opctx = new OperationContext(space:space)
    Detail detail = new Detail()
    List<Vertex> vertices = []
    // TODO: spawn thread for streaming object iterator rather than do full list construction
    for (Object id : vertexIds) {
      log.dbg("deserialize "+id)
      Map<String,Object> doc = cassDocAPI.deserializeDoc(null, null, (String)id)
      CassDocVertex vertex = new CassDocVertex(docId:id,cassDocGraph:this)
      vertices.add(vertex)
    }
    return vertices.iterator()
  }
}

