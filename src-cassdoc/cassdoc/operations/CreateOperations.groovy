package cassdoc.operations

import groovy.transform.CompileStatic

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils

import cassdoc.CommandExecServices
import cassdoc.Detail
import cassdoc.FieldValue
import cassdoc.FixedAttr
import cassdoc.IDUtil
import cassdoc.OperationContext
import cassdoc.Rel
import cassdoc.commands.mutate.NewAttr
import cassdoc.commands.mutate.NewDoc
import cassdoc.commands.mutate.NewRel
import cassdoc.commands.mutate.UpdFixedCol

import com.datastax.driver.core.DataType
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

import cwdrg.lg.annotation.Log


@Log
@CompileStatic
class CreateOperations {

  // array "stream" of docs
  // TODO: special streaming execution mode: not immediate, nor do we wait until the end.
  public static String newDocStream(CommandExecServices svcs, OperationContext opctx, Detail detail, Reader inputListOfJsonDocs, Writer outputListOfIDs)
  {
    outputListOfIDs << '['
    JsonParser parser = svcs.jsonFactory.createParser(inputListOfJsonDocs)
    JsonToken firsttoken = parser.nextToken()
    boolean firstid = true
    if (firsttoken != JsonToken.START_ARRAY) {
      throw log.err("", new Exception("newDocStream operation must begin with JSON START_ARRAY token"))
    } else {
      while (true) {
        JsonToken startObjToken = parser.nextToken()
        if (startObjToken == JsonToken.END_ARRAY) {
          outputListOfIDs << ']'
          break
        }
        else if (startObjToken != JsonToken.START_OBJECT) {
          // return list of objects created so far? no rollback....
          outputListOfIDs << ']'
          throw log.err("", new Exception("newDocStream operation, one of the array elements is not a doc"))
        } else {
          JsonToken idtoken = parser.nextToken();
          if (idtoken != JsonToken.FIELD_NAME) {
            outputListOfIDs << ']'
            throw log.err("",new Exception ("newDocStream: NO ID FIELD FOR NEW DOCUMENT"))
          } else {
            String firstField = parser.getCurrentName();
            if (!svcs.idField.equals(firstField)) {
              outputListOfIDs << ']'
              throw log.err("",new Exception ("newDocStream:ID FIELD IS NOT FIRST FIELD"))
            } else {
              String newEntityUUID = newChildDoc(svcs,opctx,detail,parser,null,null)
              if (firstid) {firstid = false; outputListOfIDs << '"' << newEntityUUID << '"'}
              else {outputListOfIDs << ',"' << newEntityUUID << '"'}
            }
          }
        }
      }
    }
  }

  public static String newDoc(CommandExecServices svcs, OperationContext opctx, Detail detail, String json) {

    // TODO: or input stream, or byte[], or others....
    JsonParser parser = svcs.jsonFactory.createParser(json)

    JsonToken firsttoken = parser.nextToken();
    if (firsttoken == JsonToken.START_OBJECT) {

      JsonToken idtoken = parser.nextToken();
      if (idtoken != JsonToken.FIELD_NAME) {
        throw new Exception ("NO ID FIELD FOR NEW DOCUMENT");
      } else {
        String firstField = parser.getCurrentName();
        if (!svcs.idField.equals(firstField)) {
          throw new Exception("ID FIELD IS NOT FIRST FIELD");
        } else {
          String newEntityUUID = newChildDoc(svcs,opctx,detail,parser,null,null)
          return newEntityUUID
        }
      }
    } else {
      throw new Exception("Invalid Parser state")
    }
  }

  public static void newAttr(CommandExecServices svcs, OperationContext opctx, Detail detail, String docUUID, String attr, String json)
  {
    JsonParser parser = svcs.jsonFactory.createParser(json)
    NewAttr cmd = new NewAttr(docUUID:docUUID, attrName:attr)
    cmd.attrValue = parseField(svcs,opctx,detail,docUUID,attr,parser)
    cmd.isComplete = true;
    analyzeNewAttrEvent(svcs,opctx,detail,cmd)
  }

  // ---- parsing helper methods

  public static String newChildDoc(CommandExecServices svcs, OperationContext opctx, Detail detail, JsonParser parser, String parentUUID, String parentAttr) {

    // parser should be pointing at the idKey field right now
    NewDoc newDocCmd = new NewDoc();
    newDocCmd.docUUID = parseIDAttr(svcs,opctx,detail,parser);
    newDocCmd.parentUUID = StringUtils.isBlank(parentUUID) ? null : parentUUID
    newDocCmd.parentAttr = parentAttr
    newDocCmd.isComplete = true

    analyzeNewDocEvent(svcs,opctx,detail, newDocCmd)

    while (true) {
      JsonToken nextField = parser.nextToken();
      if (nextField == JsonToken.END_OBJECT) {
        break;
      } else if (nextField == JsonToken.FIELD_NAME) {
        String fieldName = parser.getCurrentName();
        NewAttr newAttrCmd = new NewAttr(docUUID:newDocCmd.docUUID, attrName:fieldName)
        newAttrCmd.attrValue = parseField(svcs,opctx,detail,newDocCmd.docUUID,fieldName,parser);
        newAttrCmd.isComplete = true;
        analyzeNewAttrEvent(svcs,opctx,detail,newAttrCmd)
      } else {
        throw new Exception ("ILLEGAL TOKEN TYPE AT DOCUMENT ROOT "+nextField);
      }
    }
    return newDocCmd.docUUID

  }

  // extract or generate UUID
  public static String parseIDAttr(CommandExecServices svcs, OperationContext opctx, Detail detail, JsonParser parser)
  {
    JsonToken token = parser.nextToken()
    if (token == JsonToken.VALUE_STRING) {
      String idString = parser.getText();
      if (svcs.typeSvc.isKnownSuffix(idString)) {
        return IDUtil.timeUUID() + "-" + idString
      } else {
        if (svcs.typeSvc.isKnownSuffix(IDUtil.idSuffix(idString))) {
          return idString
        } else {
          throw new Exception ("Unknown type suffix for provided UUID: "+idString)
        }
      }
    }
    // TODO: more complicated stuff? DFRef object or something similar?
    throw new Exception("ID information not provided")
  }

  public static FieldValue parseField(CommandExecServices svcs, OperationContext opctx, Detail detail, String docUUID, String fieldName, JsonParser parser)
  {
    String fieldValue
    JsonToken token = parser.nextToken();

    if (token == JsonToken.VALUE_NULL) {return null}
    if (token == JsonToken.VALUE_STRING) {return new FieldValue(type:String.class,value:parser.getText())}
    if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) { return new FieldValue(type:Boolean.class,value:parser.getText())}
    if (token == JsonToken.VALUE_NUMBER_FLOAT) {return new FieldValue(type:Float.class,value:parser.getText())}
    if (token == JsonToken.VALUE_NUMBER_INT) {return new FieldValue(type:Integer.class,value:parser.getText())}

    if (token == JsonToken.START_ARRAY) {
      StringBuilder sb = new StringBuilder()
      parseIngestChildArray(svcs,opctx,detail,sb,parser,docUUID,fieldName)
      return new FieldValue(type:List.class,value:sb.toString())
    }

    if (token == JsonToken.START_OBJECT) {
      StringBuilder sb = new StringBuilder()
      parseIngestChildObject(svcs,opctx,detail,sb,parser,docUUID,fieldName)
      return new FieldValue(type:Map.class,value:sb.toString())
    }

    // else Exception
  }


  static void parseIngestChildObject(CommandExecServices svcs, OperationContext opctx, Detail detail, StringBuilder sb, JsonParser jsonParser, String parentUUID, String parentAttr)
  {
    sb << '{'
    boolean firstField = true
    String currentField = null
    while (true) {
      JsonToken token = jsonParser.nextToken()
      if (token == JsonToken.FIELD_NAME) {
        currentField = jsonParser.getCurrentName()
        if (firstField) {
          if (svcs.idField.equals(currentField)) {

            String childUUID = newChildDoc(svcs,opctx,detail,jsonParser,parentUUID,parentAttr)
            sb << '"' << svcs.idField << '":"' << childUUID << '"}'
            return;
          } else {
            firstField = false
          }
        } else {
          sb << ","
        }
        sb <<'"' << currentField << '":'
      }
      if (token == JsonToken.VALUE_NULL) { sb << "null" }
      if (token == JsonToken.VALUE_TRUE) { sb << "true" }
      if (token == JsonToken.VALUE_FALSE) { sb << "false" }
      if (token == JsonToken.VALUE_NUMBER_FLOAT) { sb << jsonParser.getText() }
      if (token == JsonToken.VALUE_NUMBER_INT) { sb << jsonParser.getText() }
      if (token == JsonToken.VALUE_STRING) { sb << '"' << StringEscapeUtils.escapeJson(jsonParser.getText()) << '"' }

      if (token == JsonToken.START_ARRAY) {
        // recurse
        parseIngestChildArray(svcs,opctx,detail,sb,jsonParser,parentUUID,parentAttr)
      }

      if (token == JsonToken.START_OBJECT) {
        // recurse
        parseIngestChildObject(svcs,opctx,detail, sb,jsonParser,parentUUID, parentAttr)
      }

      // unrecurse...
      if (token == JsonToken.END_OBJECT) {
        sb << '}'
        return
      }
    }
  }

  static FieldValue parseIngestChildArray(CommandExecServices svcs, OperationContext opctx, Detail detail, StringBuilder sb, JsonParser jsonParser, String parentUUID, String parentAttr)
  {
    sb << '['
    boolean firstMember = true
    int idx = 0
    while (true) {
      JsonToken token = jsonParser.nextToken()
      if (token == JsonToken.END_ARRAY) {
        sb << ']'
        // unrecurse
        return
      }

      if (firstMember) {firstMember = false} else {sb << ','}

      if (token == JsonToken.VALUE_NULL) { sb << "null" }
      if (token == JsonToken.VALUE_TRUE) { sb << "true" }
      if (token == JsonToken.VALUE_FALSE) { sb << "false" }
      if (token == JsonToken.VALUE_NUMBER_FLOAT) { sb << jsonParser.getText() }
      if (token == JsonToken.VALUE_NUMBER_INT) { sb << jsonParser.getText() }
      if (token == JsonToken.VALUE_STRING) { sb << '"' << StringEscapeUtils.escapeJson(jsonParser.getText()) << '"' }

      if (token == JsonToken.START_ARRAY) {
        // recurse
        parseIngestChildArray(svcs,opctx,detail, sb,jsonParser,parentUUID,parentAttr)
      }

      if (token == JsonToken.START_OBJECT) {
        // recurse
        parseIngestChildObject(svcs,opctx,detail, sb,jsonParser,parentUUID,parentAttr)
      }
    }
  }

  static void analyzeNewDocEvent (CommandExecServices svcs, OperationContext opctx, Detail detail, NewDoc newDocCmd)
  {
    opctx.addCommand(svcs, detail, newDocCmd)

    if (newDocCmd.parentUUID != null) {

      // TODO: "xpath"

      // parent-to-child rel
      NewRel newSubdocRelCmd = new NewRel()
      newSubdocRelCmd.p1 = newDocCmd.parentUUID
      newSubdocRelCmd.ty1 = "CH"
      newSubdocRelCmd.p2 = newDocCmd.parentAttr
      newSubdocRelCmd.c1 = newDocCmd.docUUID
      opctx.addCommand(svcs, detail, newSubdocRelCmd)

      // child-to-parent rel
      NewRel newBackrefRelCmd = new NewRel()
      newBackrefRelCmd.p1 = newDocCmd.docUUID
      newBackrefRelCmd.ty1 = "-CH"
      newBackrefRelCmd.c1 = newDocCmd.parentUUID
      newBackrefRelCmd.c2 = newDocCmd.parentAttr
      opctx.addCommand(svcs, detail, newBackrefRelCmd)

    }

  }

  static void analyzeNewAttrEvent(CommandExecServices svcs, OperationContext opctx, Detail detail, NewAttr cmd)
  {
    opctx.addCommand(svcs, detail, cmd)

    // fixed attr: should this be in event???
    String suffix = IDUtil.idSuffix(cmd.docUUID)
    FixedAttr attrdef = svcs.typeSvc.getTypeForSuffix(suffix).fixedAttrMap[cmd.attrName]
    String col = attrdef?.colname
    if (col != null) {
      Object val = null
      switch (StringUtils.lowerCase(attrdef.coltype)) {
        case null:  // assume string/varchar/text if not specified
        case DataType.Name.ASCII.toString():
        case DataType.Name.TEXT.toString():
        case DataType.Name.VARCHAR.toString():
        case "string":
          val = cmd.attrValue?.value
          break;
        case DataType.Name.TIMESTAMP.toString():
        case "date":
        case "datetime":
          val = cmd.attrValue == null ? null : new Date(Long.parseLong(cmd.attrValue.value))
          break;
        case DataType.Name.BIGINT.toString():
        case "long":
        case "counter":
          val = cmd.attrValue == null ? null : Long.parseLong(cmd.attrValue.value)
          break;
        case DataType.Name.INT.toString():
        case "integer":
        case "int":
          val = cmd.attrValue == null ? null : Integer.parseInt(cmd.attrValue.value)
          break;
        case DataType.Name.BOOLEAN.toString():
        case "boolean":
          val = cmd.attrValue == null ? null : Boolean.parseBoolean(cmd.attrValue.value)
          break;
        case DataType.Name.FLOAT.toString():
        case "float":
          val = cmd.attrValue == null ? null : Float.parseFloat(cmd.attrValue.value)
          break;
        case DataType.Name.DOUBLE.toString():
        case "double":
          val = cmd.attrValue == null ? null : Double.parseDouble(cmd.attrValue.value)
          break;
        case DataType.Name.DECIMAL.toString():
        case "bigdecimal":
          val = cmd.attrValue == null ? null : new BigDecimal(cmd.attrValue.value)
          break;
        case DataType.Name.VARINT.toString():
        case "bigint":
        case "bigdecimal":
          val = cmd.attrValue == null ? null : new BigInteger(cmd.attrValue.value);
          break;
      }
      UpdFixedCol fixedcol = new UpdFixedCol(docUUID:cmd.docUUID,colName:col,value:val)
      opctx.addCommand(svcs, detail, fixedcol)
    }


    IndexOperations.processNewAttrIndexes(svcs,opctx,detail,cmd)
  }

  static void addRel(final CommandExecServices svcs, final OperationContext opctx, final Detail detail, final Rel rel)
  {
    NewRel newRelCmd = new NewRel()
    newRelCmd.p1 = rel.p1
    newRelCmd.ty1 = rel.ty1
    newRelCmd.ty2 = rel.ty2
    newRelCmd.ty3 = rel.ty3
    newRelCmd.ty4 = rel.ty4
    newRelCmd.p2 = rel.p2
    newRelCmd.p3 = rel.p3
    newRelCmd.p4 = rel.p4
    newRelCmd.c1 = rel.c1
    newRelCmd.c2 = rel.c2
    newRelCmd.c3 = rel.c3
    newRelCmd.c4 = rel.c4
    newRelCmd.link = rel.lk
    newRelCmd.d = rel.d
    // metadata isn't allowed. Must use other metadata APIs for that. to avoid the user making their own ids
    newRelCmd.execMutationCassandra(svcs, opctx, detail)
  }

}
