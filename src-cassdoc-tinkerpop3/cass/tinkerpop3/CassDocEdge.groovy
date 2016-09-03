package cass.tinkerpop3

import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Property
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils

import cassdoc.Rel

class CassDocEdge implements Edge {
  Rel rel
  CassDocGraph cassDocGraph


  @Override
  public Graph graph() {
    cassDocGraph
  }

  @Override
  public Object id() {
    relId
  }

  @Override
  public String label() {
    // get rel
    // get ... href? nned to think
  }

  @Override
  public <V> Property<V> property(String key, V value) {
    throw Element.Exceptions.propertyAdditionNotSupported();
  }

  @Override
  public void remove() {
    throw Edge.Exceptions.edgeRemovalNotSupported();
  }

  @Override
  public <V> Iterator<Property<V>> properties(String... propertyKeys) {
    // do we support edge / rel props?
    // I think we have to... need to enhance the data model

    return null;
  }

  @Override
  public Iterator<Vertex> vertices(Direction direction) {
    // get necessary Rels based on direction / label
    switch (direction) {
      case Direction.OUT:
        return IteratorUtils.of(this.graph.vertices(getBaseEdge().vertices(Direction.OUT).next().id())).next();
      case Direction.IN:
        return IteratorUtils.of(this.graph.vertices(getBaseEdge().vertices(Direction.IN).next().id())).next();
      default:
        final Iterator<Vertex> iterator = getBaseEdge().vertices(Direction.BOTH);
        return IteratorUtils.of(this.graph.vertices(iterator.next().id()).next(), this.graph.vertices(iterator.next().id()).next());

    }
  }

}
