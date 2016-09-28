package cassdoc.commands.retrieve.cassandra

import groovy.transform.CompileStatic

import org.apache.commons.lang3.StringUtils

import cassdoc.Detail
import cassdoc.OperationContext
import cassdoc.commands.retrieve.RowProcessor

import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Token


/**
 * base class for most cassandra row processors, handles the main issues of partition detection, paging / streaming, etc.
 * 
 * @author cowardlydragon
 *
 */

@CompileStatic
abstract class CassandraPagedRowProcessor extends RowProcessor {
  static String resolveConsistency(Detail detail, OperationContext opctx) {
    if (detail != null && StringUtils.isNotEmpty(detail.readConsistency))
      return detail.readConsistency
    if (StringUtils.isNotEmpty(opctx.readConsistency))
      return opctx.readConsistency
    return "ONE"
  }


  ResultSet rs = null
  private Token lastToken = null

  /**
   * This method is called for every row encountered
   * 
   * @param row
   * @return
   */
  Object[] processRow(Row row){}

  /**
   * This optional method initializes any data structures tracking data across column/clustering keys in a partition.
   * 
   */
  void initNewPartition() {}

  /**
   * This optional method is called whenever a new partition key is encountered,  in case 
   * there are some final products/packaging/processing needed before the next partition tracking is done
   * 
   * Examples: summing columns in a row, tracking column keys in a row, etc
   */
  void completeFinishedPartition() {}

  /**
   * An optional method, the code using the RowProcessor, if indicated by the newPartition stateful property, can call this method to get the
   * final products/data structures/information that has been accumulated and finalized by completeFinishedPartition()
   * 
   * @return Object[]
   */
  Object[] getFinishedPartitionData() {
    null
  }

  Object[] nextRow() {
    Row row = rs.one()
    if (row == null) {
      newPartition = false
      return null
    } else {
      rowCount++
      partitionRowCount++
      Token currentToken = row.partitionKeyToken
      if (currentToken == lastToken) {
        newPartition = false
        if (lastToken == null) {
          pageCount++ // increment to 1 as soon as we get a legitimate row
          lastToken = currentToken
        } else {
          newPartition = true
          completeFinishedPartition()
          lastToken = currentToken
          initNewPartition()
        }
      }

      if (rs.getAvailableWithoutFetching() == fetchNextPageThreshold && !rs.isFullyFetched()) {
        pageCount++;
        rs.fetchMoreResults()
      }
    }

    return processRow(row)
  }
}