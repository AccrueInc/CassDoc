package cass.tinkerpop3

import groovy.transform.CompileStatic

import org.apache.commons.lang3.StringUtils
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Property
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality

import cassdoc.Detail
import cassdoc.OperationContext
import cassdoc.Rel


@CompileStatic
class CassDocVertex implements Vertex {

  String docId
  transient CassDocGraph cassDocGraph

  @Override
  public Graph graph() {
    cassDocGraph
  }

  @Override
  public void remove() {
    throw Vertex.Exceptions.vertexRemovalNotSupported();
  }

  @Override
  public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
    throw Vertex.Exceptions.edgeAdditionsNotSupported();
  }

  @Override
  public <V> VertexProperty<V> property(Cardinality cardinality, String key, V value, Object... keyValues) {
    throw Element.Exceptions.propertyAdditionNotSupported();
  }

  @Override
  public Object id() {
    docId
  }

  @Override
  public String label() {
    // is label an id??? no... id() gets you id.
    OperationContext opctx = new OperationContext(space:cassDocGraph.space)
    Detail detail = new Detail()
    Map<String,Object> docMeta = cassDocGraph.cassDocAPI.deserializeDocMetadata(opctx, detail, docId)
    String label = docMeta["label"]

    return label
  }



  @Override
  public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
    Set<String> labels = null
    if (edgeLabels != null && edgeLabels.length > 0) {
      labels = [] as Set
      labels.addAll(edgeLabels)
    }
    OperationContext opctx = new OperationContext(space:cassDocGraph.space)
    Detail detail = new Detail()
    List<Rel> docRels = cassDocGraph.cassDocAPI.deserializeDocRels(opctx, detail, docId)
    List<Edge> edgeList = []

    for (Rel rel : docRels) {
      if (labels == null && !StringUtils.startsWith(rel.ty1,"_")) {
        edgeList.add(new CassDocEdge(cassDocGraph:cassDocGraph,Rel:rel))
      } else if (!StringUtils.startsWith(rel.ty1,"_") && labels.contains(rel.ty1)) {
        edgeList.add(new CassDocEdge(cassDocGraph:cassDocGraph,Rel:rel))
      }
    }
    return edgeList.iterator()
  }


  @Override
  public  <V> Iterator<? extends Property<V>> properties(String... propertyKeys) {
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
    Map<String,Object> docProps = cassDocGraph.cassDocAPI.deserializeDoc(opctx, detail, docId)

    for (Map.Entry<String,Object> entry : docProps.entrySet()) {
      CassDocProperty prop = new CassDocVertexProperty(cassDocGraph:cassDocGraph,docId:docId,key:entry.key,value:entry.value,vertex:this)
      props.add(prop)
    }

    return props.iterator()
  }


  // TODO: figure out how rel.ty1/ty2/ty3/ty4 relates to labels. Right now, ty1 == label.
  // TODO: reverse relations with "-" protocol, fix "up" in engine --> -CH
  @Override
  public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
    Set<String> labels = null
    if (edgeLabels != null && edgeLabels.length > 0) {
      labels = [] as Set
      labels.addAll(edgeLabels)
    }
    OperationContext opctx = new OperationContext(space:cassDocGraph.space)
    Detail detail = new Detail()
    List<Rel> docRels = cassDocGraph.cassDocAPI.deserializeDocRels(opctx, detail, docId)
    List<Vertex> vertexList = []

    for (Rel rel : docRels) {
      if (labels == null) {
        if (direction.equals(Direction.OUT) && !StringUtils.startsWith(rel.ty1,"_") && !StringUtils.startsWith(rel.ty1,"-")) {
          vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
        } else if (direction.equals(Direction.IN) && !StringUtils.startsWith(rel.ty1,"_")&& StringUtils.startsWith(rel.ty1,"-")) {
          vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
        } else if (direction.equals(Direction.BOTH) && !StringUtils.startsWith(rel.ty1,"_")) {
          vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
        }
      } else {
        if (direction.equals(Direction.OUT) && !StringUtils.startsWith(rel.ty1,"_") && !StringUtils.startsWith(rel.ty1,"-") && labels.contains(rel.ty1)) {
          vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
        } else if (direction.equals(Direction.IN) && !StringUtils.startsWith(rel.ty1,"_") && StringUtils.startsWith(rel.ty1,"-") && labels.contains("-"+rel.ty1)) {
          vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
        } else if (direction.equals(Direction.BOTH)) {
          if (rel.ty1.startsWith("-") && labels.contains(rel.ty1.substring(1))) {
            vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
          } else if (labels.contains(rel.ty1)) {
            vertexList.add(new CassDocVertex(cassDocGraph:cassDocGraph,docId:rel.c1))
          }
        }
      }
    }
    return vertexList.iterator()
  }
}

