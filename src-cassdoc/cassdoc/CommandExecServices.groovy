package cassdoc

import groovy.transform.CompileStatic

import org.springframework.beans.factory.annotation.Autowired

import cassdoc.commands.mutate.NewAttr
import cassdoc.commands.mutate.NewDoc
import cassdoc.commands.mutate.UpdFixedCol

import com.fasterxml.jackson.core.JsonFactory

import drv.cassdriver.DriverWrapper

@CompileStatic
class CommandExecServices {

  String idField = "_id";

  JsonFactory jsonFactory = new JsonFactory();

  @Autowired
  TypeConfigurationService typeSvc

  @Autowired
  DriverWrapper driver

  // TODO: service-ify and autowire
  /*
   RetrievalOperations retrieve;
   CreateOperations create;
   MergeOperations merge;
   UpdateOperations update;
   SearchOperations search;
   IndexOperations index;
   */


  // ---- COMMANDS

  // TODO: immediate vs batch mode where driver.executeDirectUpdate is performed...


  void execUpdDocFixedColUNSAFE(OperationContext opctx, Detail detail, UpdFixedCol updDocFixedColCmd)
  {
    String space = opctx.space
    String suffix = IDUtil.idSuffix(updDocFixedColCmd.docUUID)
    String colName = updDocFixedColCmd.colName
    String cql = "UPDATE ${space}.e_${suffix} SET ${colName} = ? WHERE e = ?"
    Object args = [updDocFixedColCmd.value, updDocFixedColCmd.docUUID] as Object[]
    driver.executeDirectUpdate(space,cql,args,detail.writeConsistency,opctx.operationTimestamp)
  }

  void execNewDocCmdPAXOS(OperationContext opctx, Detail detail, NewDoc newDocCmd)
  {
    String space = opctx.space
    String suffix = IDUtil.idSuffix(newDocCmd.docUUID)
    String cql = "INSERT INTO ${space}.e_${suffix} (e,zv,a0) VALUES (?,?,?) IF NOT EXISTS"
    Object[] args = [newDocCmd.docUUID, opctx.updateUUID, newDocCmd.parentUUID] as Object[]
    driver.executeDirectUpdate(space,cql,args,detail.writeConsistency,opctx.operationTimestamp)
  }

  void execNewPropCmdPAXOS(OperationContext opctx, Detail detail, NewAttr newPropCmd)
  {
    String space = opctx.space
    String suffix = IDUtil.idSuffix(newPropCmd.docUUID)
    String cql = "INSERT INTO ${space}.p_${suffix} (e,p,zv,d,t) VALUES (?,?,?,?,?) IF NOT EXISTS"
    Object[] args = [newPropCmd.docUUID, newPropCmd.attrName, opctx.updateUUID, newPropCmd.attrValue?.value, TypeConfigurationService.attrTypeCode(newPropCmd.attrValue?.type)] as Object[]
    driver.executeDirectUpdate(space,cql,args,detail.writeConsistency,opctx.operationTimestamp)
  }



}
