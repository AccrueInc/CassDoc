package cassdoc.commands.retrieve

import groovy.transform.CompileStatic
import cassdoc.CommandExecServices
import cassdoc.Detail
import cassdoc.OperationContext

@CompileStatic
abstract class RowProcessor {
  boolean newPartition
  long pageCount = 0
  long rowCount = 0
  long partitionRowCount = 0
  int fetchNextPageThreshold = 5000
  void initiateQuery(CommandExecServices svcs, OperationContext opctx, Detail detail, Object... args) {}
  Object[] nextRow() {}

  List<Object[]> getAllRows() {
    List rows = []
    Object[] row = null
    while (row = nextRow()) {
      rows.add(row)
    }
    return rows
  }
}
