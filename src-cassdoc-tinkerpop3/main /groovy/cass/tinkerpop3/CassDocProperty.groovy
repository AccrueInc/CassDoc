package cass.tinkerpop3

import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Property

class CassDocProperty<V> implements Property<V> {
  String key
  String typeCode
  V value

  String docId
  Element element

  transient CassDocGraph cassDocGraph

  @Override
  public String key() {
    return key
  }

  @Override
  public V value() throws NoSuchElementException {
    return value;
  }

  @Override
  public boolean isPresent() {
    true;
  }

  @Override
  public Element element() {
    element
  }

  @Override
  public void remove() {
    throw Property.Exceptions.propertyRemovalNotSupported()
  }
}
