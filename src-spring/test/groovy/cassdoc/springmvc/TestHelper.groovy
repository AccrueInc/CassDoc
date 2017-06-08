package cassdoc.springmvc

class TestHelper {

    String dropCreateKeyspace(String keyspace, int replicationFactor = 1) {
        """
        | DROP KEYSPACE ${keyspace};
        | CREATE KEYSPACE $keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '$replicationFactor'}  AND durable_writes = true;
        """.stripMargin()
    }

    String createEntityTable(String keyspace, String entityTypeSuffix, List fixedCols) {
        StringBuilder sb = new StringBuilder()
        sb.append("""
        | CREATE TABLE ${keyspace}.e_${entityTypeSuffix} (
        |   e text,
        |   a0 text,
        |   z_md text,
        |   zv uuid,
        """.stripMargin())

        for (List fixedCol : fixedCols) {
            sb.append fixedCol[0]+" "+fixedCol[1]+",\n"
        }

        sb.append("""
        |   PRIMARY KEY (e)
        | );
        """.stripMargin())
        return sb.toString()
    }

    String createAttrTable(String keyspace, String entityTypeSuffix) {
        """
        | CREATE TABLE ${keyspace}.p_${entityTypeSuffix} (
        |   e text,
        |   p text,
        |   d text,
        |   t text,
        |   z_md text,
        |   zv uuid,
        |   PRIMARY KEY ((e),p)
        | );
        """.stripMargin()
    }

    String createRelationTableCql(String keyspace) {
        """
        | CREATE TABLE ${keyspace}.r (
        |   p1 text,
        |   ty1 text,
        |   ty2 text,
        |   ty3 text,
        |   ty4 text,
        |   p2 text,
        |   p3 text,
        |   p4 text,
        |   c1 text,
        |   c2 text,
        |   c3 text,
        |   c4 text,
        |   d text,
        |   link text,
        |   z_md text,  
        |   PRIMARY KEY ((p1),ty1,ty2,ty3,ty4,p2,p3,p4,c1,c2,c3,c4)
        | );
        """.stripMargin()
    }

    String createIndexTableCql(String keyspace) {
        """
        | CREATE TABLE ${keyspace}.i (
        |   i1 text,
        |   i2 text,
        |   i3 text,
        |   k1 text,
        |   k2 text,
        |   k3 text,
        |   v1 text,
        |   v2 text,
        |   v3 text,
        |   id text,
        |   d text,  
        |   PRIMARY KEY ((i1,i2,i3,k1,k2,k3),v1,v2,v3)
        | );
        """.stripMargin()
    }

}
