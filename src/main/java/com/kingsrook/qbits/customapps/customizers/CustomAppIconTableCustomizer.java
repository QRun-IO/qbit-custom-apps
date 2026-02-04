/*
 * Copyright © 2022-2025. ColdTrack <contact@coldtrack.com>.  All Rights Reserved.
 */

package com.kingsrook.qbits.customapps.customizers;


import java.util.List;
import com.kingsrook.qbits.customapps.metadata.ViewScreenGenericCompositeWidgetMetaDataProducer;
import com.kingsrook.qbits.customapps.model.CustomAppIcon;
import com.kingsrook.qqq.backend.core.actions.customizers.TableCustomizerInterface;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.QueryOrGetInputInterface;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.CompositeWidgetData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.icon.IconBlockData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.icon.IconStyles;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.icon.IconValues;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.text.TextBlockData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.text.TextStyles;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.blocks.text.TextValues;
import com.kingsrook.qqq.backend.core.model.data.QRecord;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppIconTableCustomizer implements TableCustomizerInterface
{
   public static final String LABEL_COLOR = "rgb(52, 71, 103)";
   public static final String VALUE_COLOR = "rgb(123, 128, 154)";



   /***************************************************************************
    *
    ***************************************************************************/
   @Override
   public List<QRecord> postQuery(QueryOrGetInputInterface queryInput, List<QRecord> records) throws QException
   {
      for(QRecord record : records)
      {
         setIconPreview(record);
      }
      return records;
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private static IconBlockData iconBlock(String color, String iconName)
   {
      return new IconBlockData().withValues(new IconValues().withName(iconName))
         .withStyles(new IconStyles().withColor(color).withFontSize("125px"));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static TextBlockData valueTextBlock(String color, String text)
   {
      return new TextBlockData().withValues(new TextValues(text))
         .withStyles(new TextStyles().withColor(color).withSize("14").withWeight("bold"));
   }



   /***************************************************************************
    *
    ***************************************************************************/
   private static void setIconPreview(QRecord record)
   {
      CustomAppIcon customAppIcon = new CustomAppIcon(record);
      if(customAppIcon.getIconId() != null && customAppIcon.getName() != null)
      {
         ViewScreenGenericCompositeWidgetMetaDataProducer.setDividerValue(record, "divider");

         CompositeWidgetData previewComposite = new CompositeWidgetData().withLayout(CompositeWidgetData.Layout.FLEX_ROW)
            .withBlock(valueTextBlock(LABEL_COLOR, "Icon Preview: "))
            .withBlock(iconBlock(VALUE_COLOR, customAppIcon.getIconId()));

         record.setValue("iconPreview", previewComposite);
      }
   }

}
