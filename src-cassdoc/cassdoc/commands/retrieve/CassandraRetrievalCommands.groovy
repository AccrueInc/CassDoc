package cassdoc.commands.retrieve

import groovy.transform.CompileStatic
import cwdrg.lg.annotation.Log



@CompileStatic
@Log
class CassandraRetrievalCommands implements RetrievalCommands {
  RowProcessor getDocRP(String docUUID) {
    new GetDocRP(docUUID:docUUID)
  }
  RowProcessor getDocAttrsRP(String docUUID) {
    new GetDocAttrsRP(docUUID:docUUID)
  }
  RowProcessor queryToListOfStrArrRP(String query) {
    new QueryToListOfStrArr(query:query)
  }
  RowProcessor queryToListOfObjArrRP(String query) {
    new QueryToListOfObjArr(query:query)
  }
  RowProcessor indexTableRP(String i1, String i2, String i3, String k1,String k2, String k3) {
    new IndexTableRP(i1:i1,i2:i2,i3:i3,k1:k1,k2:k2,k3:k3)
  }
  RowProcessor entityTableSecondaryIndexRP(String table, String column, String columnType, Object columnValue) {
    new EntityTableSecondaryIndexRP(table:table,column:column,columnType:columnType,columnValue:columnValue)
  }
  RowProcessor docAttrListRP(String docUUID) {
    new DocAttrListRP(docUUID:docUUID)
  }
}
