/*
 * Copyright © 2022-2024. ColdTrack <contact@coldtrack.com>.  All Rights Reserved.
 */

package com.kingsrook.qbits.customapps.customizers;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.kingsrook.qbits.customapps.model.CustomAppContainer;
import com.kingsrook.qbits.customapps.utils.CustomAppPermissionUtils;
import com.kingsrook.qbits.customapps.utils.CustomAppsUtils;
import com.kingsrook.qbits.userrolepermissions.model.Permission;
import com.kingsrook.qqq.backend.core.actions.customizers.RecordCustomizerUtilityInterface;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizerInterface;
import com.kingsrook.qqq.backend.core.actions.tables.DeleteAction;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.metadata.TableMetaDataInput;
import com.kingsrook.qqq.backend.core.model.actions.metadata.TableMetaDataOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.delete.DeleteInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.update.UpdateInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.utils.ValueUtils;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppContainerTableCustomizer implements TableCustomizerInterface
{

   /*******************************************************************************
    ** custom actions to run after table meta data was created by the meta data action
    **
    *******************************************************************************/
   @Override
   public void postMetaDataAction(TableMetaDataInput tableMetaDataInput, TableMetaDataOutput tableMetaDataOutput) throws QException
   {
      /////////////////////////////////
      // add dynamic chips for icons //
      /////////////////////////////////
      CustomAppsUtils.addDynamicIconChipAdornments(tableMetaDataOutput.getTable(), "customAppIconId");
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public List<QRecord> preInsert(InsertInput insertInput, List<QRecord> records, boolean isPreview) throws QException
   {
      if(!isPreview)
      {
         preInsertOrUpdate(records, Optional.empty());
      }
      return (records);
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public List<QRecord> preUpdate(UpdateInput updateInput, List<QRecord> records, boolean isPreview, Optional<List<QRecord>> oldRecordList) throws QException
   {
      if(!isPreview)
      {
         preInsertOrUpdate(records, oldRecordList);
      }
      return (records);
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   private void preInsertOrUpdate(List<QRecord> records, Optional<List<QRecord>> oldRecordList) throws QException
   {
      Optional<Map<Serializable, QRecord>> oldRecordMap = oldRecordListToMap("id", oldRecordList);

      for(QRecord record : records)
      {
         ///////////////////////////////////////////////////////////////////////
         // create a permission for this container (or insert if none exists) //
         ///////////////////////////////////////////////////////////////////////
         String             name       = ValueUtils.getValueAsString(RecordCustomizerUtilityInterface.getValueFromRecordElseFromOldRecord("name", record, record.getValue("id"), oldRecordMap));
         CustomAppContainer container  = new CustomAppContainer(record).withName(name);
         Permission         permission = CustomAppPermissionUtils.buildOrGetPermission(container.getName(), CustomAppContainer.TABLE_NAME);
         record.setValue("permissionId", permission.getId());
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public List<QRecord> postDelete(DeleteInput deleteInput, List<QRecord> records) throws QException
   {
      List<Serializable> permissionIds = new ArrayList<>();
      for(QRecord record : records)
      {
         ////////////////////////////////////////////
         // delete a permission for this container //
         ////////////////////////////////////////////
         CustomAppContainer container    = new CustomAppContainer(record);
         Integer            permissionId = container.getPermissionId();
         permissionIds.add(permissionId);
      }
      new DeleteAction().execute(new DeleteInput(Permission.TABLE_NAME).withPrimaryKeys(permissionIds));

      return TableCustomizerInterface.super.postDelete(deleteInput, records);
   }

}
