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
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.data.QAssociation;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.fields.ValueTooLongBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildJoin;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildRecordListWidget;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.ChildTable;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;
import com.kingsrook.qqq.backend.core.model.metadata.tables.Association;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.SectionFactory;
import com.kingsrook.qqq.backend.core.model.metadata.tables.UniqueKey;


/*******************************************************************************
 ** QRecord Entity for CustomAppSection table
 *******************************************************************************/
@QMetaDataProducingEntity(
   producePossibleValueSource = true,
   produceTableMetaData = true,
   tableMetaDataCustomizer = CustomAppSection.TableMetaDataCustomizer.class,
   childTables = {
      @ChildTable(
         childTableEntityClass = CustomApp.class,
         joinFieldName = "customAppSectionId",
         childJoin = @ChildJoin(enabled = true),
         childRecordListWidget = @ChildRecordListWidget(label = "Apps", enabled = true, maxRows = 250, canAddChildRecords = true))
   }
)
public class CustomAppSection extends QRecordEntity implements Serializable
{
   public static final String TABLE_NAME                   = "customAppSection";
   public static final String ASSOCIATION_NAME_CUSTOM_APPS = "customApps";



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
         String appsChildJoinName = QJoinMetaData.makeInferredJoinName(TABLE_NAME, CustomApp.TABLE_NAME);

         table
            .withLabel("Section")
            .withUniqueKey(new UniqueKey("name"))
            .withIcon(new QIcon().withName("account_tree"))
            .withRecordLabelFormat("%s")
            .withRecordLabelFields("name")
            .withSection(SectionFactory.defaultT1("id"))
            .withSection(SectionFactory.defaultT2("customAppContainerId", "name", "sequenceNo"))
            .withSection(SectionFactory.customT2("apps", new QIcon("polyline")).withWidgetName(appsChildJoinName))
            .withSection(SectionFactory.defaultT3("createDate", "modifyDate"))

            .withAssociation(new Association().withName(ASSOCIATION_NAME_CUSTOM_APPS).withJoinName(appsChildJoinName).withAssociatedTableName(CustomAppSection.TABLE_NAME));

         return (table);
      }
   }



   @QField(isEditable = false, isPrimaryKey = true)
   private Integer id;

   @QField(maxLength = 100, valueTooLongBehavior = ValueTooLongBehavior.ERROR, isRequired = true)
   private String name;

   @QField(label = "Container", isRequired = true, possibleValueSourceName = CustomAppContainer.TABLE_NAME)
   private Integer customAppContainerId;

   @QField(isRequired = true)
   private Integer sequenceNo;

   @QField(isEditable = false)
   private Instant createDate;

   @QField(isEditable = false)
   private Instant modifyDate;

   @QAssociation(name = ASSOCIATION_NAME_CUSTOM_APPS)
   private List<CustomApp> apps;



   /*******************************************************************************
    ** Default constructor
    *******************************************************************************/
   public CustomAppSection()
   {
   }



   /*******************************************************************************
    ** Constructor that takes a QRecord
    *******************************************************************************/
   public CustomAppSection(QRecord record)
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
   public CustomAppSection withId(Integer id)
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
   public CustomAppSection withName(String name)
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
   public CustomAppSection withCreateDate(Instant createDate)
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
   public CustomAppSection withModifyDate(Instant modifyDate)
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
   public CustomAppSection withSequenceNo(Integer sequenceNo)
   {
      this.sequenceNo = sequenceNo;
      return (this);
   }



   /*******************************************************************************
    ** Getter for customAppContainerId
    *******************************************************************************/
   public Integer getCustomAppContainerId()
   {
      return (this.customAppContainerId);
   }



   /*******************************************************************************
    ** Setter for customAppContainerId
    *******************************************************************************/
   public void setCustomAppContainerId(Integer customAppContainerId)
   {
      this.customAppContainerId = customAppContainerId;
   }



   /*******************************************************************************
    ** Fluent setter for customAppContainerId
    *******************************************************************************/
   public CustomAppSection withCustomAppContainerId(Integer customAppContainerId)
   {
      this.customAppContainerId = customAppContainerId;
      return (this);
   }



   /*******************************************************************************
    ** Getter for apps
    *******************************************************************************/
   public List<CustomApp> getApps()
   {
      return (this.apps);
   }



   /*******************************************************************************
    ** Setter for apps
    *******************************************************************************/
   public void setApps(List<CustomApp> apps)
   {
      this.apps = apps;
   }



   /*******************************************************************************
    ** Fluent setter for apps
    *******************************************************************************/
   public CustomAppSection withApps(List<CustomApp> apps)
   {
      this.apps = apps;
      return (this);
   }

}
