package cass.tinkerpop3

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Property

import cassdoc.Detail
import cassdoc.OperationContext

@CompileStatic
class CassDocEdgeProperty<V> extends CassDocProperty<V> {

  CassDocEdge edge

  @Override
  public boolean isPresent() {
    true
  }

  public Object id() {
    return [docId, key] as String[]
  }

  public <V> Property<V> property(String key, V value) {
    throw Element.Exceptions.propertyAdditionNotSupported()
  }

  @Override
  public Element element() {
    edge
  }

  public <U> Iterator<Property<U>> properties(String... propertyKeys) {
    OperationContext opctx = new OperationContext(space:cassDocGraph.space)
    Detail detail = new Detail()

    if (propertyKeys != null) {
      Set<String> propnames = [] as Set
      propnames.addAll(propertyKeys)
      if (propnames.size() > 0) {
        detail.setAttrSubset(propnames)
      }
    }

    List<Property> props = []
    Map<String,Object> docMeta = cassDocGraph.cassDocAPI.deserializeDoc(opctx, detail, docId)
    String metadataId = docMeta["_id"]

    for (Map.Entry<String,Object> entry : docMeta.entrySet()) {
      CassDocProperty prop = new CassDocProperty(docId:metadataId, element:edge, key:entry.key, value:entry.value)
      props.add(prop)
    }

    return props.iterator()
  }
}
