package cass.tinkerpop3

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Property
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality


@CompileStatic
class CassDocVertex implements Vertex {

  String docId;
  CassDocGraph cassDocGraph

  @Override
  public Graph graph() {
    cassDocGraph
  }


  @Override
  public void remove() {
    if (this instanceof Vertex)
      throw Vertex.Exceptions.vertexRemovalNotSupported();
    else
      throw Edge.Exceptions.edgeRemovalNotSupported();
  }

  @Override
  public Object id() {
    return docId;
  }

  @Override
  public String label() {
    cassDocGraph.cassDocAPI.getSimpleAttr(/*opctx*/null,/*dtl*/null,docId,"label");
  }

  @Override
  public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
    //throw Vertex.Exceptions.edgeAdditionsNotSupported("Tinkerpop edge addition not supported, use CassDoc to add relations between documents");
  }

  @Override
  public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
    // TODO: add get Rels to API
    // all proper cassdoc relations should also have reverse direction noted...
    // edgelabels must filter the ty...
  }

  @Override
  public  <V> Iterator<? extends Property<V>> properties(String... propertyKeys) {
    // TODO: get property subset api? already exists via detail?
    // 1) get document properties/attributes/fields
    // 2) filter for property key subset

    return null;
  }

  @Override
  public <V> VertexProperty<V> property(Cardinality cardinality, String key, V value, Object... keyValues) {
    throw Element.Exceptions.propertyAdditionNotSupported();
  }


  @Override
  public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
    // get document relations
    // filter on the basis of edge labels and directionality (relation types presumably)
    return null;
  }
}
