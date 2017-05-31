package cassdoc

import groovy.transform.CompileStatic

import org.apache.commons.lang3.StringUtils


@CompileStatic
class Detail {
  String readConsistency = null
  String writeConsistency = null
  Long writeTimestampMicros = null
  Long timeoutMillis = null
  // TODO: deletion cascade modes

  Set<String> attrSubset = null
  Set<String> attrExclude = null
  Map<String,Detail> attrDetail = null

  boolean pullChildDocs = false
  Detail childDocDetail = null
  Map<String,Detail> childDocSuffixDetail = null

  // meta attributes
  boolean docIDTimestampMeta = false // y
  boolean docIDDateMeta = false // y
  String docWritetimeMeta = null // y
  String attrWritetimeMeta = null // y
  boolean docWritetimeDateMeta = false // y
  boolean attrWritetimeDateMeta = false //y
  boolean docPaxosMeta = false  // y
  boolean attrPaxosMeta = false // y
  boolean docPaxosTimestampMeta = false  // y
  boolean attrPaxosTimestampMeta = false // y
  boolean docPaxosDateMeta = false  // y
  boolean attrPaxosDateMeta = false // y
  boolean docMetaIDMeta = false // y
  boolean docMetaDataMeta = false // y
  boolean attrMetaIDMeta = false  // y
  boolean attrMetaDataMeta = false // y
  //boolean relMetaIDMeta = false // TODO
  //boolean relMetaDataMeta = false // TODO
  boolean parentMeta = false // y
  boolean docChildrenMeta = false // y
  boolean docRelationsMeta = false // y
  boolean docTokenMeta = false // y
  boolean attrTokenMeta = false // y

  // TODO: relation retrieval settings

  String searchStartToken = null
  String searchStopToken = null
  String searchEntityLimit = null
  Integer fetchNextPageThreshold = null
  Integer fetchPageSize = null

  // batch vs async spray vs as-you-go specifiers...


  // Async vs Streaming vs "DOM" is API signature specific

  Detail resolveAttrDetail(String attr)
  {
    if (attrSubset != null) {
      if (!attrSubset.contains(attr)) {
        return null
      }
    }
    if (attrExclude != null) {
      if (attrExclude.contains(attr)) {
        return null
      }
    }
    if (attrDetail == null) { return this }
    Detail attrDtl = attrDetail[attr]
    if (attrDtl == null) { return this }
    return attrDtl
  }


  Detail resolveChildDocDetail(String childDocUUID, String attr)
  {
    // no attr detail necessary, since we have already done so in the attribute iteration
    if (pullChildDocs) {
      if (childDocSuffixDetail != null) {
        String suffix = IDUtil.idSuffix(childDocUUID)
        Detail suffixDetail = childDocSuffixDetail?.get(suffix)
        if (suffixDetail != null) {
          return suffixDetail
        }
      }
      if (childDocDetail != null) {
        return childDocDetail
      } else {
        // no detail provided, but we are exhorted to pull child docs, so we'll return the last detail provided
        return this
      }

    } else  {
      // suffix overrides can override pullChildDocs global flag
      if (childDocSuffixDetail != null) {
        String suffix = IDUtil.idSuffix(childDocUUID)
        Detail suffixDetail = childDocSuffixDetail?.get(suffix)
        if (suffixDetail != null) {
          return suffixDetail
        }
      }

    }
    // return null to indicate do NOT pull child docs
    return null
  }

  String resolveReadConsistency(Detail detail, OperationContext opctx)
  {
    StringUtils.isEmpty(detail?.readConsistency) ? opctx.readConsistency : detail.readConsistency
  }

  String resolveWriteConsistency(Detail detail, OperationContext opctx)
  {
    StringUtils.isEmpty(detail?.writeConsistency) ? opctx.writeConsistency : detail.writeConsistency
  }


}
