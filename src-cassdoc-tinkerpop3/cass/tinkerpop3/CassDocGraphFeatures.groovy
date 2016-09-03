package cass.tinkerpop3

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.VertexProperty
import org.apache.tinkerpop.gremlin.structure.Graph.Features.EdgeFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.EdgePropertyFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.ElementFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.GraphFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.VariableFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.VertexFeatures
import org.apache.tinkerpop.gremlin.structure.Graph.Features.VertexPropertyFeatures


public class CassDocGraphFeatures implements GraphFeatures {

  @Override
  public boolean supportsConcurrentAccess() {
    return false;
  }

  @Override
  public boolean supportsComputer() {
    return false;
  }

  @Override
  public VariableFeatures variables() {
    return new CassDocVariableFeatures();
  }

  @Override
  public boolean supportsThreadedTransactions() {
    return false;
  }
}


public class CassDocElementFeatures implements ElementFeatures {

  @Override
  public boolean supportsUserSuppliedIds() {
    return false;
  }

  @Override
  public boolean supportsStringIds() {
    return false;
  }

  @Override
  public boolean supportsUuidIds() {
    return false;
  }

  @Override
  public boolean supportsAnyIds() {
    return false;
  }

  @Override
  public boolean supportsCustomIds() {
    return false;
  }
}

public class CassDocVertexFeatures implements VertexFeatures {
  @Override
  public VertexPropertyFeatures properties() {
    return new CassDocVertexPropertyFeatures();
  }

  @Override
  public boolean supportsMetaProperties() {
    return false
  }

  @Override
  public boolean supportsMultiProperties() {
    return false
  }

  @Override
  public boolean supportsUserSuppliedIds() {
    return false;
  }

  @Override
  public VertexProperty.Cardinality getCardinality(final String key) {
    return VertexProperty.Cardinality.single
  }
}

public class CassDocEdgeFeatures implements EdgeFeatures {
  @Override
  public EdgePropertyFeatures properties() {
    return new CassDocEdgePropertyFeatures();
  }
}

public class CassDocEdgePropertyFeatures implements EdgePropertyFeatures {
  @Override
  public boolean supportsMapValues() {
    return false;
  }

  @Override
  public boolean supportsMixedListValues() {
    return false;
  }

  @Override
  public boolean supportsSerializableValues() {
    return false;
  }

  @Override
  public boolean supportsUniformListValues() {
    return false;
  }
}

public class CassDocVariableFeatures implements Graph.Features.VariableFeatures {
  @Override
  public boolean supportsBooleanValues() {
    return false;
  }

  @Override
  public boolean supportsDoubleValues() {
    return false;
  }

  @Override
  public boolean supportsFloatValues() {
    return false;
  }

  @Override
  public boolean supportsIntegerValues() {
    return false;
  }

  @Override
  public boolean supportsLongValues() {
    return false;
  }

  @Override
  public boolean supportsMapValues() {
    return false;
  }

  @Override
  public boolean supportsMixedListValues() {
    return false;
  }

  @Override
  public boolean supportsByteValues() {
    return false;
  }

  @Override
  public boolean supportsBooleanArrayValues() {
    return false;
  }

  @Override
  public boolean supportsByteArrayValues() {
    return false;
  }

  @Override
  public boolean supportsDoubleArrayValues() {
    return false;
  }

  @Override
  public boolean supportsFloatArrayValues() {
    return false;
  }

  @Override
  public boolean supportsIntegerArrayValues() {
    return false;
  }

  @Override
  public boolean supportsLongArrayValues() {
    return false;
  }

  @Override
  public boolean supportsStringArrayValues() {
    return false;
  }

  @Override
  public boolean supportsSerializableValues() {
    return false;
  }

  @Override
  public boolean supportsStringValues() {
    return true;
  }

  @Override
  public boolean supportsUniformListValues() {
    return false;
  }
}



public class CassDocVertexPropertyFeatures implements VertexPropertyFeatures {

  @Override
  public boolean supportsMapValues() {
    return false;
  }

  @Override
  public boolean supportsMixedListValues() {
    return false;
  }

  @Override
  public boolean supportsSerializableValues() {
    return false;
  }

  @Override
  public boolean supportsUniformListValues() {
    return false;
  }

  @Override
  public boolean supportsUserSuppliedIds() {
    return false;
  }

  @Override
  public boolean supportsAnyIds() {
    return false;
  }
}
