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
import java.util.List;
import com.kingsrook.qbits.customapps.customizers.CustomAppContainerTableCustomizer;
import com.kingsrook.qbits.customapps.metadata.CustomAppContainerPlaceBeforeContainerPVSMetaDataProducer;
import com.kingsrook.qbits.userrolepermissions.model.Permission;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizers;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QAssociation;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildJoin;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildRecordListWidget;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildTable;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Association;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QFieldSection;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Tier;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for CustomAppContainer table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = CustomAppContainer.TableMetaDataCustomizer.class,
   childTables = {
      @ChildTable(
         childTableEntityClass = CustomAppSection.class,
         joinFieldName = "customAppContainerId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Sections", enabled = true, maxRows = 250, canAddChildRecords = true))
   }
)
public class CustomAppContainer extends QRecordEntity implements Serializable
{
   public static final String TABLE_NAME                           = "customAppContainer";
   public static final String ASSOCIATION_NAME_CUSTOM_APP_SECTIONS = "customAppSections";



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
         String sectionsChildJoinName = QJoinMetaData.makeInferredJoinName(TABLE_NAME, CustomAppSection.TABLE_NAME);

         table
            .withLabel("Container")
            .withUniqueKey(new UniqueKey("name"))
            .withIcon(new QIcon().withName("account_tree"))
            .withRecordLabelFormat("%s")
            .withRecordLabelFields("name")
            .withSection(SectionFactory.defaultT1("id"))
            .withSection(SectionFactory.defaultT2("name", "customAppIconId", "placeBeforeContainer", "sequenceNo", "permissionId"))
            .withSection(SectionFactory.customT2("apps", new QIcon("polyline")).withWidgetName(sectionsChildJoinName))
            .withSection(new QFieldSection("users", new QIcon().withName("person"), Tier.T2).withWidgetName("customAppContainerPermissions"))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"))

            .withCustomizer(TableCustomizers.POST_META_DATA_ACTION, new QCodeReference(CustomAppContainerTableCustomizer.class))
            .withCustomizer(TableCustomizers.POST_DELETE_RECORD, new QCodeReference(CustomAppContainerTableCustomizer.class))
            .withCustomizer(TableCustomizers.POST_QUERY_RECORD, new QCodeReference(CustomAppContainerTableCustomizer.class))
            .withCustomizer(TableCustomizers.PRE_INSERT_RECORD, new QCodeReference(CustomAppContainerTableCustomizer.class))
            .withCustomizer(TableCustomizers.PRE_UPDATE_RECORD, new QCodeReference(CustomAppContainerTableCustomizer.class))

            .withAssociation(new Association().withName(ASSOCIATION_NAME_CUSTOM_APP_SECTIONS).withJoinName(sectionsChildJoinName).withAssociatedTableName(CustomAppSection.TABLE_NAME));

         table.getField("customAppIconId").withFieldAdornment(AdornmentType.CHIP);

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR, isRequired = true)
   private String name;

   @QField(label = "Icon", isRequired = true, possibleValueSourceName = CustomAppIcon.TABLE_NAME)
   private Integer customAppIconId;

   @QField(possibleValueSourceName = CustomAppContainerPlaceBeforeContainerPVSMetaDataProducer.NAME)
   private String placeBeforeContainer;

   @QField()
   private Integer sequenceNo;

   @QField(isEditable = false, possibleValueSourceName = Permission.TABLE_NAME)
   private Integer permissionId;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;

   @QAssociation(name = ASSOCIATION_NAME_CUSTOM_APP_SECTIONS)
   private List<CustomAppSection> sections;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public CustomAppContainer()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public CustomAppContainer(QRecord record)
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
   public CustomAppContainer withId(Integer id)
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
   public CustomAppContainer withName(String name)
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
   public CustomAppContainer withCreateDate(Instant createDate)
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
   public CustomAppContainer withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return (this);
   }



   /*******************************************************************************
    ** Getter for placeBeforeContainer
    *******************************************************************************/
   public String getPlaceBeforeContainer()
   {
      return (this.placeBeforeContainer);
   }



   /*******************************************************************************
    ** Setter for placeBeforeContainer
    *******************************************************************************/
   public void setPlaceBeforeContainer(String placeBeforeContainer)
   {
      this.placeBeforeContainer = placeBeforeContainer;
   }



   /*******************************************************************************
    ** Fluent setter for placeBeforeContainer
    *******************************************************************************/
   public CustomAppContainer withPlaceBeforeContainer(String placeBeforeContainer)
   {
      this.placeBeforeContainer = placeBeforeContainer;
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
   public CustomAppContainer withCustomAppIconId(Integer customAppIconId)
   {
      this.customAppIconId = customAppIconId;
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
   public CustomAppContainer withPermissionId(Integer permissionId)
   {
      this.permissionId = permissionId;
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
   public CustomAppContainer withSequenceNo(Integer sequenceNo)
   {
      this.sequenceNo = sequenceNo;
      return (this);
   }



   /*******************************************************************************
    ** Getter for sections
    *******************************************************************************/
   public List<CustomAppSection> getSections()
   {
      return (this.sections);
   }



   /*******************************************************************************
    ** Setter for sections
    *******************************************************************************/
   public void setSections(List<CustomAppSection> sections)
   {
      this.sections = sections;
   }



   /*******************************************************************************
    ** Fluent setter for sections
    *******************************************************************************/
   public CustomAppContainer withSections(List<CustomAppSection> sections)
   {
      this.sections = sections;
      return (this);
   }

}
