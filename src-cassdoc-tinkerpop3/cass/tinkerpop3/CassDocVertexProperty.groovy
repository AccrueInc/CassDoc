package cass.tinkerpop3

import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Property
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty

class CassDocVertexProperty<V> implements VertexProperty<V> {

  String key
  String typeCode
  V value

  CassDocVertex vertex
  CassDocGraph cassDocGraph


  @Override
  public String key() {
    key
  }

  @Override
  public V value() throws NoSuchElementException {
    value
  }

  @Override
  public boolean isPresent() {
    cassDocGraph.cassDocAPI.hasAttr()
  }

  @Override
  public void remove() {
    throw VertexProperty.Exceptions.metaPropertiesNotSupported();
  }

  @Override
  public Object id() {
    return vertex.docId+"-"+key
  }

  @Override
  public <V> Property<V> property(String key, V value) {
    throw VertexProperty.Exceptions.metaPropertiesNotSupported();
  }

  @Override
  public Vertex element() {
    vertex
  }

  @Override
  public <U> Iterator<Property<U>> properties(String... propertyKeys) {
    throw VertexProperty.Exceptions.metaPropertiesNotSupported();
  }
}
