package cassdoc.commands.retrieve;

public interface RetrievalCommands {
  RowProcessor getDocAttrsRP(String docUUID)
  RowProcessor queryToListOfStrArrRP(String query)
  RowProcessor queryToListOfObjArrRP(String query)
  RowProcessor indexTableRP(String i1, String i2, String i3, String k1,String k2, String k3)
  RowProcessor entityTableSecondaryIndexRP(String table, String column, String columnType, Object columnValue)
  RowProcessor docAttrListRP(String docUUID)
}