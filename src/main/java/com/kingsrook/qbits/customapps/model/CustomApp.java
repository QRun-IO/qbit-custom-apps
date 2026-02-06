/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kingsrook.qbits.customapps.model;


import java.io.Serializable;
import java.time.Instant;
import com.kingsrook.qbits.customapps.customizers.CustomAppTableCustomizer;
import com.kingsrook.qbits.userrolepermissions.model.Permission;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizers;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QFieldSection;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Tier;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for CustomApp table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = CustomApp.TableMetaDataCustomizer.class
)
public class CustomApp extends QRecordEntity implements Serializable
{
   public static final String TABLE_NAME = "customApp";



   /***************************************************************************
    **
    ***************************************************************************/
   public static class TableMetaDataCustomizer implements MetaDataCustomizerInterface<QTableMetaData>
   {
      /***************************************************************************
       **
       ***************************************************************************/
      @Override
      public QTableMetaData customizeMetaData(QInstance qInstance, QTableMetaData table) throws QException
      {
         table
            .withLabel("App")
            .withUniqueKey(new UniqueKey("name"))
            .withIcon(new QIcon().withName("account_tree"))
            .withRecordLabelFormat("%s")
            .withRecordLabelFields("name")
            .withSection(SectionFactory.defaultT1("id"))
            .withSection(SectionFactory.defaultT2("name", "customAppIconId", "customAppBackendConfigId", "customAppSectionId", "sequenceNo", "lookerDashboardId", "permissionId"))
            .withSection(new QFieldSection("users", new QIcon().withName("person"), Tier.T2).withWidgetName("customAppPermissions"))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"))

            .withCustomizer(TableCustomizers.POST_META_DATA_ACTION, new QCodeReference(CustomAppTableCustomizer.class))
            .withCustomizer(TableCustomizers.POST_DELETE_RECORD, new QCodeReference(CustomAppTableCustomizer.class))
            .withCustomizer(TableCustomizers.PRE_INSERT_RECORD, new QCodeReference(CustomAppTableCustomizer.class))
            .withCustomizer(TableCustomizers.PRE_UPDATE_RECORD, new QCodeReference(CustomAppTableCustomizer.class));

         table.getField("customAppIconId").withFieldAdornment(AdornmentType.CHIP);

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR, isRequired = true)
   private String name;

   @QField(label = "Backend Config", isRequired = true, possibleValueSourceName = CustomAppBackendConfig.TABLE_NAME)
   private Integer customAppBackendConfigId;

   @QField(label = "Section", isRequired = true, possibleValueSourceName = CustomAppSection.TABLE_NAME)
   private Integer customAppSectionId;

   @QField(label = "Icon", isRequired = true, possibleValueSourceName = CustomAppIcon.TABLE_NAME)
   private Integer customAppIconId;

   @QField(isRequired = true)
   private Integer sequenceNo;

   @QField(isEditable = false, possibleValueSourceName = Permission.TABLE_NAME)
   private Integer permissionId;

   @QField(isRequired = true)
   private Integer lookerDashboardId;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public CustomApp()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public CustomApp(QRecord record)
   {
      populateFromQRecord(record);
   }



   /*******************************************************************************
    ** Getter for id
    *******************************************************************************/
   public Integer getId()
   {
      return (this.id);
   }



   /*******************************************************************************
    ** Setter for id
    *******************************************************************************/
   public void setId(Integer id)
   {
      this.id = id;
   }



   /*******************************************************************************
    ** Fluent setter for id
    *******************************************************************************/
   public CustomApp withId(Integer id)
   {
      this.id = id;
      return (this);
   }



   /*******************************************************************************
    ** Getter for name
    *******************************************************************************/
   public String getName()
   {
      return (this.name);
   }



   /*******************************************************************************
    ** Setter for name
    *******************************************************************************/
   public void setName(String name)
   {
      this.name = name;
   }



   /*******************************************************************************
    ** Fluent setter for name
    *******************************************************************************/
   public CustomApp withName(String name)
   {
      this.name = name;
      return (this);
   }



   /*******************************************************************************
    ** Getter for createDate
    *******************************************************************************/
   public Instant getCreateDate()
   {
      return (this.createDate);
   }



   /*******************************************************************************
    ** Setter for createDate
    *******************************************************************************/
   public void setCreateDate(Instant createDate)
   {
      this.createDate = createDate;
   }



   /*******************************************************************************
    ** Fluent setter for createDate
    *******************************************************************************/
   public CustomApp withCreateDate(Instant createDate)
   {
      this.createDate = createDate;
      return (this);
   }



   /*******************************************************************************
    ** Getter for modifyDate
    *******************************************************************************/
   public Instant getModifyDate()
   {
      return (this.modifyDate);
   }



   /*******************************************************************************
    ** Setter for modifyDate
    *******************************************************************************/
   public void setModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
   }



   /*******************************************************************************
    ** Fluent setter for modifyDate
    *******************************************************************************/
   public CustomApp withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return (this);
   }



   /*******************************************************************************
    ** Getter for sequenceNo
    *******************************************************************************/
   public Integer getSequenceNo()
   {
      return (this.sequenceNo);
   }



   /*******************************************************************************
    ** Setter for sequenceNo
    *******************************************************************************/
   public void setSequenceNo(Integer sequenceNo)
   {
      this.sequenceNo = sequenceNo;
   }



   /*******************************************************************************
    ** Fluent setter for sequenceNo
    *******************************************************************************/
   public CustomApp withSequenceNo(Integer sequenceNo)
   {
      this.sequenceNo = sequenceNo;
      return (this);
   }



   /*******************************************************************************
    ** Getter for customAppBackendConfigId
    *******************************************************************************/
   public Integer getCustomAppBackendConfigId()
   {
      return (this.customAppBackendConfigId);
   }



   /*******************************************************************************
    ** Setter for customAppBackendConfigId
    *******************************************************************************/
   public void setCustomAppBackendConfigId(Integer customAppBackendConfigId)
   {
      this.customAppBackendConfigId = customAppBackendConfigId;
   }



   /*******************************************************************************
    ** Fluent setter for customAppBackendConfigId
    *******************************************************************************/
   public CustomApp withCustomAppBackendConfigId(Integer customAppBackendConfigId)
   {
      this.customAppBackendConfigId = customAppBackendConfigId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for customAppSectionId
    *******************************************************************************/
   public Integer getCustomAppSectionId()
   {
      return (this.customAppSectionId);
   }



   /*******************************************************************************
    ** Setter for customAppSectionId
    *******************************************************************************/
   public void setCustomAppSectionId(Integer customAppSectionId)
   {
      this.customAppSectionId = customAppSectionId;
   }



   /*******************************************************************************
    ** Fluent setter for customAppSectionId
    *******************************************************************************/
   public CustomApp withCustomAppSectionId(Integer customAppSectionId)
   {
      this.customAppSectionId = customAppSectionId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for customAppIconId
    *******************************************************************************/
   public Integer getCustomAppIconId()
   {
      return (this.customAppIconId);
   }



   /*******************************************************************************
    ** Setter for customAppIconId
    *******************************************************************************/
   public void setCustomAppIconId(Integer customAppIconId)
   {
      this.customAppIconId = customAppIconId;
   }



   /*******************************************************************************
    ** Fluent setter for customAppIconId
    *******************************************************************************/
   public CustomApp withCustomAppIconId(Integer customAppIconId)
   {
      this.customAppIconId = customAppIconId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for lookerDashboardId
    *******************************************************************************/
   public Integer getLookerDashboardId()
   {
      return (this.lookerDashboardId);
   }



   /*******************************************************************************
    ** Setter for lookerDashboardId
    *******************************************************************************/
   public void setLookerDashboardId(Integer lookerDashboardId)
   {
      this.lookerDashboardId = lookerDashboardId;
   }



   /*******************************************************************************
    ** Fluent setter for lookerDashboardId
    *******************************************************************************/
   public CustomApp withLookerDashboardId(Integer lookerDashboardId)
   {
      this.lookerDashboardId = lookerDashboardId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for permissionId
    *******************************************************************************/
   public Integer getPermissionId()
   {
      return (this.permissionId);
   }



   /*******************************************************************************
    ** Setter for permissionId
    *******************************************************************************/
   public void setPermissionId(Integer permissionId)
   {
      this.permissionId = permissionId;
   }



   /*******************************************************************************
    ** Fluent setter for permissionId
    *******************************************************************************/
   public CustomApp withPermissionId(Integer permissionId)
   {
      this.permissionId = permissionId;
      return (this);
   }

}
