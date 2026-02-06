/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2024.  Kingsrook, LLC
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
package com.kingsrook.qbits.customapps.metadata;


import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.AbstractWidgetRenderer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetInput;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetOutput;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.CompositeWidgetData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.WidgetType;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.divider.DividerBlockData;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.FieldAdornment;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;


/*******************************************************************************
 * Meta Data Producer for ViewScreenGenericCompositeWidget
 *
 * e.g., generic widget to be used on table virtual fields, to display a composite
 * block widget.
 *******************************************************************************/
public class CustomAppsGenericCompositeWidgetMetaDataProducer extends MetaDataProducer<QWidgetMetaData>
{
   public static final String NAME = "ViewScreenGenericCompositeWidget";



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QWidgetMetaData produce(QInstance qInstance) throws QException
   {
      QWidgetMetaData genericCompositeBlockWidget = new QWidgetMetaData()
         .withName(NAME)
         .withType(WidgetType.COMPOSITE.getType())
         .withShowReloadButton(false)
         .withShowExportButton(false)
         .withIsCard(false)
         .withCodeReference(new QCodeReference(NoopWidgetRenderer.class));

      return genericCompositeBlockWidget;
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static <T extends QFieldMetaData> T applyToField(T field)
   {
      field.withFieldAdornment(new FieldAdornment(AdornmentType.WIDGET)
         .withValue(AdornmentType.WidgetValues.WIDGET_NAME, CustomAppsGenericCompositeWidgetMetaDataProducer.NAME));
      return (field);
   }



   /***************************************************************************
    * for a virtual field that is being used to draw a horizontal divider line,
    * set the appropriate widget-data value for the field in a record.
    ***************************************************************************/
   public static void setDividerValue(QRecord record, String fieldName)
   {
      CompositeWidgetData compositeWidgetData = new CompositeWidgetData()
         .withBlock(new DividerBlockData());
      record.setValue(fieldName, compositeWidgetData);
   }



   /***************************************************************************
    ** this widget - is not rendered in the traditional way, but rather,
    ** its data comes from the record's field, so noop to render here.
    ***************************************************************************/
   public static class NoopWidgetRenderer extends AbstractWidgetRenderer
   {
      /***************************************************************************
       **
       ***************************************************************************/
      @Override
      public RenderWidgetOutput render(RenderWidgetInput input) throws QException
      {
         return null;
      }
   }
}
